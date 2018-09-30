package io.ticktok.support;

import com.google.gson.Gson;
import io.ticktok.register.Clock;
import io.ticktok.register.ClockChannel;
import org.rockm.blink.BlinkRequest;
import org.rockm.blink.BlinkServer;

import java.io.IOException;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

public class TicktockServiceStub {

    public static final String TICKTOK_SERVICE_DOMAIN = "http://localhost:9999";
    public static final String TOKEN = "Bfmx3Z7y9GxY4yLrKP";

    private BlinkServer ticktokService;
    public ClockRequest lastClockRequest;

    public TicktockServiceStub(int port, boolean validResponse) throws IOException {
        ticktokService = new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) -> {
                validateToken(req);
                lastClockRequest = new Gson().fromJson(req.body(), ClockRequest.class);
                return createClockFrom(req, validResponse);
            });
        }};
    }

    private void validateToken(BlinkRequest req) {
        assert req.header(AUTHORIZATION).equals(TOKEN);
    }

    private String createClockFrom(BlinkRequest req, boolean validResponse) {
        return new Gson().toJson(Clock.builder().
                id("123").
                schedule(extractBody(req)).
                url(TickPublisher.QUEUE_HOST).
                clockChannel(ClockChannel.builder().exchange(TickPublisher.QUEUE_EXCHANGE).topic("myTopic").uri(validResponse ? "amqp://localhost:5672" : "badUri").build()).
                build());
    }

    private String extractBody(BlinkRequest req) {
        return new Gson().fromJson(req.body(), ClockRequest.class).schedule;
    }

    public class ClockRequest {
        public String schedule = "";

        public ClockRequest(String schedule) {
            this.schedule = schedule;
        }
    }

    public void stop() {
        ticktokService.stop();
    }
}
