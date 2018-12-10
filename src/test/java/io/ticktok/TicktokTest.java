package io.ticktok;

import io.ticktok.support.TickPublisher;
import io.ticktok.support.TicktockServiceStub;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.ticktok.support.TicktockServiceStub.TICKTOK_SERVICE_DOMAIN;
import static io.ticktok.support.TicktockServiceStub.TOKEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TicktokTest {

    private static final String EVERY_5_SECONDS = "every.5.seconds";
    private static TicktockServiceStub ticktockServiceStub;

    @BeforeAll
    public static void init() {
        ticktockServiceStub = new TicktockServiceStub(9999, true);
    }

    @Test
    public void registerNewClock() {
        // TODO - TicktokOptions should be builder
        register(() -> {}); // schedule
        assertThat(ticktockServiceStub.lastClockRequest.schedule, is(EVERY_5_SECONDS));
        assertThat(ticktockServiceStub.lastClockRequest.name, is("my_clock"));
    }

    private void register(Runnable runnable) {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock("my_clock").on(EVERY_5_SECONDS).invoke(runnable);
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

    @AfterAll
    public static void tearDown() {
        ticktockServiceStub.stop();
    }

}