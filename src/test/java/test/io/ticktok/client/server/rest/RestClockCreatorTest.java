package test.io.ticktok.client.server.rest;

import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.ConnectionException;
import io.ticktok.client.server.FailToCreateClockException;
import io.ticktok.client.server.rest.RestClockCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rockm.blink.BlinkServer;

import java.io.IOException;

import static io.ticktok.client.Ticktok.options;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RestClockCreatorTest {

    public static final int PORT = 9191;
    private static final String DOMAIN = "http://localhost:" + PORT;

    private BlinkServer server;

    @Test
    void failOnBadRequest() throws IOException {
        server = new BlinkServer(PORT) {{
            post("/api/v1/clocks", (req, res) -> {
                res.status(400);
                return "";
            });
        }};
        assertThrows(FailToCreateClockException.class, this::createClock);
    }

    private void createClock() {
        new RestClockCreator(options().domain(DOMAIN).token("my_token"))
                .create(new ClockRequest("my-pupu-clock", "every.5.seconds"));
    }

    @Test
    void failOnServiceNotAvailable() {
        assertThrows(ConnectionException.class, this::createClock);
    }

    @AfterEach
    void tearDown() {
        if(server != null) {
            server.stop();
        }
    }
}
