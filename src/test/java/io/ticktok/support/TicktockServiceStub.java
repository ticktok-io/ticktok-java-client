package io.ticktok.support;

import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import io.javalin.Javalin;
import io.ticktok.register.Clock;
import io.ticktok.register.Channel;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class TicktockServiceStub {

    public static final String TICKTOK_SERVICE_DOMAIN = "http://localhost:9999";
    public static final String TOKEN = "my_token";

    public ClockRequest lastClockRequest;
    private final Javalin app;

    public TicktockServiceStub(int port, boolean validResponse) {
        app = Javalin.create().disableStartupBanner().enableCaseSensitiveUrls().start(port).post("/api/v1/clocks", ctx -> {
            validateToken(ctx.queryParam("access_token"));
            lastClockRequest = new Gson().fromJson(ctx.body(), ClockRequest.class);
            createQueueFor();
            ctx.result(createClockFrom(ctx.body(), validResponse)).status(201);
        });
    }

    private void createQueueFor() throws IOException, TimeoutException {
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
                channel(Channel.builder().queue(lastClockRequest.name).uri(validResponse ? "amqp://localhost:5672" : "badUri").build()).
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
