package io.ticktok.client.support;

import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import io.ticktok.client.register.Clock;
import io.ticktok.client.register.RabbitChannel;
import io.ticktok.client.rest.ClockRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rockm.blink.BlinkServer;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class TicktockServerStub {

    public static final String DOMAIN = "http://localhost:9999";
    public static final String TOKEN = "my_token";
    private static final String INVALID_SCHEDULE = "invalid";

    public ClockRequest lastClockRequest;
    private final BlinkServer app;

    public TicktockServerStub(int port, boolean validResponse) throws IOException {
        app = new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) -> {
                lastClockRequest = new Gson().fromJson(req.body(), ClockRequest.class);
                try {
                    validateSchedule(lastClockRequest.getSchedule());
                } catch(StatusCodeException e) {
                    res.status(e.getStatus());
                    return "";
                }


                validateToken(req.param("access_token"));
                try {
                    createQueueFor();
                } catch (TimeoutException | IOException e) {
                    res.status(500);
                    return "Fail to create channel";
                }
                res.status(201);
                return createClockFrom(req.body(), validResponse);
            });
        }};
    }

    private void validateSchedule(String schedule) {
        if(INVALID_SCHEDULE.equals(schedule)) throw new StatusCodeException(400);
    }

    private void createQueueFor() throws TimeoutException, IOException {
        com.rabbitmq.client.Channel channel = new ConnectionFactory().newConnection().createChannel();
        channel.queueDeclare(lastClockRequest.getName(), false, false, false, null);
    }

    private void validateToken(String token) {
        assert Objects.requireNonNull(token).equals(TOKEN);
    }

    private String createClockFrom(String body, boolean validResponse) {
        return new Gson().toJson(Clock.builder().
                id("123").
                schedule(extractBody(body)).
                url(TickPublisher.QUEUE_HOST).
                channel(RabbitChannel.builder().queue(lastClockRequest.getName()).uri(validResponse ? "amqp://localhost:5672" : "badUri").build()).
                name(lastClockRequest.getName()).
                build());
    }

    private String extractBody(String body) {
        return new Gson().fromJson(body, ClockRequest.class).getSchedule();
    }

    public void stop() {
        app.stop();
    }

    @Getter
    @AllArgsConstructor
    private class StatusCodeException extends RuntimeException {
        private int status;
    }
}
