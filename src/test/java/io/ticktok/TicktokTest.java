package io.ticktok;

import io.ticktok.register.Clock;
import io.ticktok.support.QueueClient;
import io.ticktok.support.TicktockServiceStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TicktokTest {

    private static final String TICKTOK_SERVICE_DOMAIN = "http://localhost:9999";
    private static final String EVERY_5_SECONDS = "every.5.seconds";
    private static final String TOKEN = "Bfmx3Z7y9GxY4yLrKP";
    private TicktockServiceStub ticktockServiceStub;
    private QueueClient qClient;

    @Before
    public void init() throws IOException {
        qClient = new QueueClient();
        ticktockServiceStub = new TicktockServiceStub(9999);
    }

    @Test
    public void registerNewClock() throws IOException {
        // TODO - TicktokOptions should be builder
        tick();
        assertThat(ticktockServiceStub.lastClockRequest.schedule, is(EVERY_5_SECONDS));
    }

    private void tick() throws IOException {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS, () -> {});
    }

    @Test
    public void invokeOnTick() throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Clock clock = new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS, countDownLatch::countDown);
        assert countDownLatch.getCount() == 1;
        qClient.publishTick(clock.getClockChannel().getExchange());
        try {
            countDownLatch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // pass
    }

    @After
    public void tearDown() {
        ticktockServiceStub.stop();
    }

}