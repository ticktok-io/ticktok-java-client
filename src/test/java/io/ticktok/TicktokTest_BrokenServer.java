package io.ticktok;

import io.ticktok.support.TicktockServiceStub;
import org.junit.jupiter.api.Test;

import static io.ticktok.support.TicktockServiceStub.TICKTOK_SERVICE_DOMAIN;
import static io.ticktok.support.TicktockServiceStub.TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TicktokTest_BrokenServer {

    private static final String EVERY_5_SECONDS = "every.5.seconds";

    @Test
    public void failOnServerDown() {
        assertThrows(Ticktok.TicktokException.class, () -> register(() -> {}));
    }

    private void register(Runnable runnable) {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock("my_clock").on(EVERY_5_SECONDS).invoke(runnable);
    }

    @Test
    public void failOnQueueDown() {
        TicktockServiceStub stub =  new TicktockServiceStub(9999, false);
        assertThrows(Ticktok.TicktokException.class, () -> register(() -> {}));
        stub.stop();
    }
}
