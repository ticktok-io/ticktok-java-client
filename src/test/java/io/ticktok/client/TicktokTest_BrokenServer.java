package io.ticktok.client;

import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.support.ServerStub;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.ticktok.client.Ticktok.options;
import static io.ticktok.client.support.ServerStub.DOMAIN;
import static io.ticktok.client.support.ServerStub.TOKEN;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicktokTest_BrokenServer {

    private static final String EVERY_5_SECONDS = "every.5.seconds";

    @Disabled
    @Test
    void failOnServerDown() {
        assertThrows(TicktokException.class, () -> register(() -> {
        }));
    }

    private void register(Runnable runnable) {
        new Ticktok(options().domain(DOMAIN).token(TOKEN)).newClock("my_clock").on(EVERY_5_SECONDS).invoke(runnable);
    }

    @Disabled
    @Test
    void failOnQueueDown() throws Exception {
        ServerStub stub = new ServerStub(9999, false);
        assertThrows(ClockRequest.TicktokInvalidValueException.class, () -> register(() -> {
        }));
        stub.stop();
    }
}
