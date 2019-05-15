package test.io.ticktok.client.support;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.tick.TickChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.rockm.blink.BlinkServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static io.ticktok.client.tick.rabbit.RabbitTickerPolicy.QUEUE_PARAM;
import static io.ticktok.client.tick.rabbit.RabbitTickerPolicy.URI_PARAM;
import static io.ticktok.client.tick.TickListener.RABBIT;
import static org.testng.Assert.assertTrue;

public class ServerStub {

    public static final String DOMAIN = "http://localhost:9999";
    public static final String TOKEN = "my_access_token";
    public static final String CLOCK_ID = "123";
    private static final String INVALID_SCHEDULE = "invalid";

    public ClockRequest lastClockRequest;
    private final BlinkServer app;

    private final Map<String, Boolean> clockTick = new HashMap<>();
    private Connection connection;
    private Channel channel;
    private String latestTick;

    public ServerStub(int port) throws Exception {
        createConnection();
        app = new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) -> {
                ClockRequest clockRequest = new Gson().fromJson(req.body(), ClockRequest.class);
                try {
                    validateToken(req.param("access_token"));
                    validateSchedule(clockRequest.getSchedule());
                } catch (StatusCodeException e) {
                    res.status(e.getStatus());
                    return "";
                }
                final Clock clock = clockFrom(clockRequest);
                try {
                    channel.queueDeclare(clock.getChannel().getDetails().get(QUEUE_PARAM), false, false, true, null);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create queue", e);
                }
                lastClockRequest = clockRequest;
                res.status(201);
                return new Gson().toJson(clock);
            });

            put("/api/v1/clocks/{id}/tick", (req, res) -> {
                res.status(204);
                final String id = req.pathParam("id");
                if (clockTick.get(id) != null) {
                    clockTick.put(id, true);
                }
                return "";
            });

            get("/api/v1/clocks", (req, res) -> {
                final Clock clock = clockFrom(new ClockRequest(req.param("name"), req.param("schedule")));
                clockTick.put(clock.getId(), false);
                return new Gson().toJson(clock);
            });

        }};
    }

    private void createConnection() throws TimeoutException, IOException {
        connection = new ConnectionFactory().newConnection();
        channel = connection.createChannel();
    }

    private void validateToken(String token) {
        if (!TOKEN.equals(token)) throw new StatusCodeException(401);
    }

    private void validateSchedule(String schedule) {
        if (INVALID_SCHEDULE.equals(schedule)) throw new StatusCodeException(400);
    }

    private Clock clockFrom(ClockRequest request) {
        String id = clockIdFor(request.getName(), request.getSchedule());
        return Clock.builder().
                id(id).
                name(request.getName()).
                schedule(request.getSchedule()).
                url(DOMAIN + "/api/v1/clocks/" + id).
                channel(TickChannel.builder()
                        .type(RABBIT)
                        .details(ImmutableMap.of(URI_PARAM, "amqp://localhost:5672", QUEUE_PARAM, request.getName()))
                        .build()).
                build();
    }

    private String clockIdFor(String name, String schedule) {
        return DigestUtils.md5Hex(name + schedule).toUpperCase();
    }

    public void tick(String name) throws IOException {
        channel.basicPublish("", name, null, "tick".getBytes());
    }

    public void stop() throws IOException, TimeoutException {
        app.stop();
        channel.close();
        connection.close();
    }

    public String latestTick() {
        return latestTick;
    }

    public void gotTickedFor(String name, String schedule) {
        assertTrue(clockTick.getOrDefault(clockIdFor(name, schedule), false), name + " Has not been ticked");
    }

    @Getter
    @AllArgsConstructor
    private class StatusCodeException extends RuntimeException {
        private int status;
    }
}
