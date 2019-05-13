package test.io.ticktok.client.server;

import io.ticktok.client.server.ConnectionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestClockActionBase_TicktokNotReachable extends ClockActionBaseTest {

    @Test
    void failOnServiceNotAvailable() {
        assertThrows(ConnectionException.class, () -> createClockWith("my-pupu-clock","every.5.seconds"));
    }
}
