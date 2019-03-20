package io.ticktok.client;

import io.ticktok.client.rest.ClockRequest;
import io.ticktok.client.support.TicktockServiceStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.ticktok.client.support.TicktockServiceStub.TICKTOK_SERVICE_DOMAIN;
import static io.ticktok.client.support.TicktockServiceStub.TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicktokTest_BrokenServer {

    private static final String EVERY_5_SECONDS = "every.5.seconds";

    @Test
    void failOnServerDown() {
        assertThrows(TicktokException.class, () -> register(() -> {}));
    }

    private void register(Runnable runnable) {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock("my_clock").on(EVERY_5_SECONDS).invoke(runnable);
    }

    @Test
    void failOnQueueDown() throws IOException {
        TicktockServiceStub stub =  new TicktockServiceStub(9999, false);
        assertThrows(ClockRequest.TicktokInvalidValueException.class, () -> register(() -> {}));
        stub.stop();
    }
}
