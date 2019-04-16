package test.io.ticktok.client.support;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import test.io.ticktok.client.server.Clock;
import test.io.ticktok.client.server.ClockRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rockm.blink.BlinkServer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ServerStub {

    public static final String DOMAIN = "http://localhost:9999";
    public static final String TOKEN = "my_access_token";
    public static final String CLOCK_ID = "123";
    private static final String INVALID_SCHEDULE = "invalid";
    private static final String QUEUE = "java-client-queue-test";

    public ClockRequest lastClockRequest;
    private final BlinkServer app;

    private Connection connection;
    private Channel channel;


    public ServerStub(int port, boolean validResponse) throws Exception {
        createQueueFor();
        app = new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) -> {
                lastClockRequest = new Gson().fromJson(req.body(), ClockRequest.class);
                try {
                    validateToken(req.param("access_token"));
                    validateSchedule(lastClockRequest.getSchedule());
                } catch(StatusCodeException e) {
                    res.status(e.getStatus());
                    return "";
                }
                res.status(201);
                return createClockFrom(clockRequestFrom(req.body()));
            });
        }};
    }

    private void createQueueFor() throws TimeoutException, IOException {
        connection = new ConnectionFactory().newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE, false, false, false, null);
    }

    private void validateToken(String token) {
        if(!TOKEN.equals(token)) throw new StatusCodeException(401);
    }

    private void validateSchedule(String schedule) {
        if(INVALID_SCHEDULE.equals(schedule)) throw new StatusCodeException(400);
    }

    private String createClockFrom(ClockRequest request) {
        return new Gson().toJson(Clock.builder().
                id(CLOCK_ID).
                name(request.getName()).
                schedule(request.getSchedule()).
                url(DOMAIN + "/api/v1/clocks/" + CLOCK_ID).
                channel(Clock.TickChannel.builder().queue(QUEUE).uri("amqp://localhost:5672").build()).
                build());
    }

    private ClockRequest clockRequestFrom(String body) {
        return new Gson().fromJson(body, ClockRequest.class);
    }

    public void tick() throws IOException {
        channel.basicPublish("", QUEUE, null, "tick".getBytes());
    }

    public void stop() throws IOException, TimeoutException {
        app.stop();
        channel.close();
        connection.close();
    }

    @Getter
    @AllArgsConstructor
    private class StatusCodeException extends RuntimeException {
        private int status;
    }
}
