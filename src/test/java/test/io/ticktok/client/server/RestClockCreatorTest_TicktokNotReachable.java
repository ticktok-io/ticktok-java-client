package test.io.ticktok.client.server;

import io.ticktok.client.Ticktok;
import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.ConnectionException;
import io.ticktok.client.server.rest.RestClockCreator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestClockCreatorTest_TicktokNotReachable {

    private static final String DOMAIN = "http://localhost:" + 1212;
    private static final String TOKEN = "my_token";

    @Test
    void failOnServiceNotAvailable() {
        assertThrows(ConnectionException.class, this::createClock);
    }

    private Clock createClock() {
        return new RestClockCreator(Ticktok.options().domain(DOMAIN).token(TOKEN))
                .create(new ClockRequest("my-pupu-clock","every.5.seconds"));
    }
}
