package io.ticktok.client.support;

import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import io.ticktok.client.register.Clock;
import io.ticktok.client.register.RabbitChannel;
import org.rockm.blink.BlinkServer;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class TicktockServiceStub {

    public static final String TICKTOK_SERVICE_DOMAIN = "http://localhost:9999";
    public static final String TOKEN = "my_token";

    public ClockRequest lastClockRequest;
    private final BlinkServer app;

    public TicktockServiceStub(int port, boolean validResponse) throws IOException {
        app = new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) -> {
                validateToken(req.param("access_token"));
                lastClockRequest = new Gson().fromJson(req.body(), ClockRequest.class);
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

    private void createQueueFor() throws TimeoutException, IOException {
        com.rabbitmq.client.Channel channel = new ConnectionFactory().newConnection().createChannel();
        channel.queueDeclare(lastClockRequest.name, false, false, false, null);
    }

    private void validateToken(String token) {
        assert Objects.requireNonNull(token).equals(TOKEN);
    }

    private String createClockFrom(String body, boolean validResponse) {
        return new Gson().toJson(Clock.builder().
                id("123").
                schedule(extractBody(body)).
                url(TickPublisher.QUEUE_HOST).
                channel(RabbitChannel.builder().queue(lastClockRequest.name).uri(validResponse ? "amqp://localhost:5672" : "badUri").build()).
                name(lastClockRequest.name).
                build());
    }

    private String extractBody(String body) {
        return new Gson().fromJson(body, ClockRequest.class).schedule;
    }

    public class ClockRequest {
        public String schedule;
        public String name;

        public ClockRequest(String schedule, String name) {
            this.schedule = schedule;
            this.name = name;
        }
    }

    public void stop() {
        app.stop();
    }
}
