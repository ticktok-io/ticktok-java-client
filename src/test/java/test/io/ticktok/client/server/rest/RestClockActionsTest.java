package test.io.ticktok.client.server.rest;

import com.google.gson.Gson;
import io.ticktok.client.server.*;
import io.ticktok.client.server.rest.RestClockActions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rockm.blink.BlinkServer;

import java.io.IOException;

import static io.ticktok.client.Ticktok.options;
import static org.junit.jupiter.api.Assertions.assertThrows;


class RestClockActionsTest {

    public static final ClockRequest CLOCK_REQUEST = new ClockRequest("kuku", "every.13.minutes");
    public static final int PORT = 9191;

    private BlinkServer server;

    @Test
    void failOnConnectionError() {
        // No server
        assertThrows(ConnectionException.class, () ->
                tickOnDomain("http://non-existing-server"));
    }

    private void tickOnDomain(String domain) {
        new RestClockActions(options().domain(domain).token("-")).tick(CLOCK_REQUEST);
    }

    @Test
    void failOnClockNotFound() throws IOException {
        server = new BlinkServer(PORT) {{
                get("/api/v1/clocks", (req, res) -> {
                    if (req.param("name").equals(CLOCK_REQUEST.getName()) &&
                            req.param("schedule").equals(CLOCK_REQUEST.getSchedule())) {
                        res.status(404);
                    }
                    return "";
                });
            }};

        assertThrows(ClockNotFoundException.class, () -> tickOnDomain("http://localhost:" + PORT));
    }

    @Test
    void failOnFailureToTick() throws IOException {
        server = new BlinkServer(PORT) {{
                get("/api/v1/clocks", (req, res) -> new Gson().toJson(Clock.builder().id("123")));
                put("/api/v1/clocks/123/tick", (req, res) -> {
                    res.status(500);
                    return "";
                });
            }};

        assertThrows(FailToActOnClockException.class, () -> tickOnDomain("http://localhost:" + PORT));
    }

    @AfterEach
    void tearDown() {
        if(server != null) {
            server.stop();
        }
    }
}