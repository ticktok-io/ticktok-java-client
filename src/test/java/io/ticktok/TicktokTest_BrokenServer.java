package io.ticktok;

import io.ticktok.support.TicktockServiceStub;
import org.junit.Test;

import java.io.IOException;

import static io.ticktok.support.TicktockServiceStub.TICKTOK_SERVICE_DOMAIN;
import static io.ticktok.support.TicktockServiceStub.TOKEN;

public class TicktokTest_BrokenServer {

    private static final String EVERY_5_SECONDS = "every.5.seconds";

    @Test(expected = Ticktok.TicktokException.class)
    public void failOnServerDown() {
        register(() -> {});
    }

    private void register(Runnable runnable) {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS, runnable);
    }

    @Test(expected = Ticktok.TicktokException.class)
    public void failOnQueueDown() throws IOException {
        TicktockServiceStub stub =  new TicktockServiceStub(9999, false);
        register(() -> {});
        stub.stop();
    }
}
