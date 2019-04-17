package test.io.ticktok.client.server;

import io.ticktok.client.Ticktok;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.ConnectionException;
import io.ticktok.client.server.FailToCreateClockException;
import io.ticktok.client.server.RestClockCreator;
import org.junit.jupiter.api.Test;
import org.rockm.blink.BlinkServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RestClockCreatorTest {

    public static final int PORT = 1212;

    @Test
    void failOnServiceNotAvailable() {
        assertThrows(ConnectionException.class, () -> createClockWithSchedule("every.5.seconds"));
    }

    private void createClockWithSchedule(String schedule) {
        new RestClockCreator(Ticktok.options().domain("http://localhost:" + PORT).token("my_token"))
                .create(new ClockRequest("my-pupu-clock", schedule));
    }

    @Test
    void failOnBadRequest() throws IOException {
        BlinkServer stub = badRequestServer();
        assertThrows(FailToCreateClockException.class, () -> createClockWithSchedule("-"));
        stub.stop();
    }

    private BlinkServer badRequestServer() throws IOException {
        return new BlinkServer(PORT) {{
            post("/api/v1/clocks", (req, res) -> {
                res.status(400);
                return "";
            });
        }};
    }
}