package io.ticktok;

import io.ticktok.support.TickPublisher;
import io.ticktok.support.TicktockServiceStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.ticktok.support.TicktockServiceStub.TICKTOK_SERVICE_DOMAIN;
import static io.ticktok.support.TicktockServiceStub.TOKEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class TicktokTest {

    private static final String EVERY_5_SECONDS = "every.5.seconds";
    private static TicktockServiceStub ticktockServiceStub;

    @BeforeEach
    public void init() {
        ticktockServiceStub = new TicktockServiceStub(9999, true);
    }

    @Test
    public void registerNewClock() {
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

    @AfterEach
    public void tearDown() {
        ticktockServiceStub.stop();
    }

}