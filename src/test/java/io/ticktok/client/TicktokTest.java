package io.ticktok.client;

import io.ticktok.client.support.TickPublisher;
import io.ticktok.client.support.TicktockServerStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static io.ticktok.client.Ticktok.options;
import static io.ticktok.client.support.TicktockServerStub.DOMAIN;
import static io.ticktok.client.support.TicktockServerStub.TOKEN;
import static java.time.Duration.ofSeconds;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class TicktokTest {

    private static final String SCHEDULE = "every.5.seconds";
    public static final String NAME = "my_clock";

    private final Ticktok ticktok = new Ticktok(options().domain(DOMAIN).token(TOKEN));
    private TicktockServerStub server;

    @BeforeEach
    void init() throws IOException {
        server = new TicktockServerStub(9999, true);
    }

    @Test
    void registerNewClock() {
        ticktok.schedule("kuku", "every.11.seconds");
        assertThat(server.lastClockRequest.getName(), is("kuku"));
        assertThat(server.lastClockRequest.getSchedule(), is("every.1.seconds"));
    }

    @Test
    void failOnInvalidSchedule() {
        assertThrows(TicktokException.class, () -> ticktok.schedule("kuku", "invalid"));
    }

    @Test
    void invokeOnTick() throws Exception {
        CountDownLatch waitForTickLatch = new CountDownLatch(1);
        ticktok.schedule(NAME, SCHEDULE).onTick(waitForTickLatch::countDown);
        verifyCallbackWasntDoneSynchronicity(waitForTickLatch);
        TickPublisher.publish();
        assertTimeoutPreemptively(ofSeconds(3), (Executable) waitForTickLatch::await);
        // pass
    }

    private void verifyCallbackWasntDoneSynchronicity(CountDownLatch countDownLatch) {
        assert countDownLatch.getCount() == 1;
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

}