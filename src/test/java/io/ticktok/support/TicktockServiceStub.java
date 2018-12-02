package io.ticktok.support;

import com.google.gson.Gson;
import io.javalin.Javalin;
import io.ticktok.register.Clock;
import io.ticktok.register.ClockChannel;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class TicktockServiceStub {

    public static final String TICKTOK_SERVICE_DOMAIN = "http://localhost:9999";
    public static final String TOKEN = "Bfmx3Z7y9GxY4yLrKP";

    public ClockRequest lastClockRequest;
    private final Javalin app;

    public TicktockServiceStub(int port, boolean validResponse) {
        app = Javalin.create().start(port);
        app.post("/api/v1/clocks", ctx -> {
            validateToken(ctx.req.getHeader(AUTHORIZATION));
            lastClockRequest = new Gson().fromJson(ctx.body(), ClockRequest.class);
            ctx.result(createClockFrom(ctx.body(), validResponse));
        });
    }

    private void validateToken(String token) {
        assert token.equals(TOKEN);
    }

    private String createClockFrom(String body, boolean validResponse) {
        return new Gson().toJson(Clock.builder().
                id("123").
                schedule(extractBody(body)).
                url(TickPublisher.QUEUE_HOST).
                clockChannel(ClockChannel.builder().exchange(TickPublisher.QUEUE_EXCHANGE).topic("myTopic").uri(validResponse ? "amqp://localhost:5672" : "badUri").build()).
                build());
    }

    private String extractBody(String body) {
        return new Gson().fromJson(body, ClockRequest.class).schedule;
    }

    public class ClockRequest {
        public String schedule = "";
        public String name = "";

        public ClockRequest(String schedule, String name) {
            this.schedule = schedule;
            this.name = name;
        }
    }

    public void stop() {
        app.stop();
    }
}
