package io.ticktok;

import io.ticktok.support.TicktockServiceStub;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static io.ticktok.support.TicktockServiceStub.TICKTOK_SERVICE_DOMAIN;
import static io.ticktok.support.TicktockServiceStub.TOKEN;

public class TicktokBrokenServerTest {

    private TicktockServiceStub stub;
    private static final String EVERY_5_SECONDS = "every.5.seconds";

    @Test(expected = Ticktok.TicktokException.class)
    public void failOnServerDown() {
        register(() -> {});
    }

    @Test(expected = Ticktok.TicktokException.class)
    public void failOnQueueDown() throws IOException {
       stub =  new TicktockServiceStub(9999, false);
        register(() -> {});
    }

    private void register(Runnable runnable) {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS, runnable);
    }

    @After
    public void tearDown(){
        if (stub != null){
            stub.stop();
        }
    }
}
