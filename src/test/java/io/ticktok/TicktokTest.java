package io.ticktok;

import io.ticktok.support.TickPublisher;
import io.ticktok.support.TicktockServiceStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.ticktok.support.TicktockServiceStub.TICKTOK_SERVICE_DOMAIN;
import static io.ticktok.support.TicktockServiceStub.TOKEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TicktokTest {

    private static final String EVERY_5_SECONDS = "every.5.seconds";
    private TicktockServiceStub ticktockServiceStub;

    @Before
    public void init() throws IOException {
        ticktockServiceStub = new TicktockServiceStub(9999, true);
    }

    @Test
    public void registerNewClock() throws IOException {
        // TODO - TicktokOptions should be builder
        register(() -> {}); // schedule
        assertThat(ticktockServiceStub.lastClockRequest.schedule, is(EVERY_5_SECONDS));
    }

    private void register(Runnable runnable) throws IOException {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS, runnable);
    }

    @Test
    public void invokeOnTick() throws Exception {
        CountDownLatch waitForTickLatch = new CountDownLatch(1);
        register(waitForTickLatch::countDown);
        verifyCallbackWasntDoneSynchronicity(waitForTickLatch);
        TickPublisher.publish();
        waitForTickLatch.await(3, TimeUnit.SECONDS);
        // pass
    }

    private void verifyCallbackWasntDoneSynchronicity(CountDownLatch countDownLatch) {
        assert countDownLatch.getCount() == 1;
    }

    @After
    public void tearDown() {
        ticktockServiceStub.stop();
    }

}