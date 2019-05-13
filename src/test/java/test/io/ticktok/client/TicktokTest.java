package test.io.ticktok.client;

import io.ticktok.client.Ticktok;
import io.ticktok.client.TicktokException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import test.io.ticktok.client.support.ServerStub;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static io.ticktok.client.Ticktok.options;
import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static test.io.ticktok.client.support.ServerStub.DOMAIN;
import static test.io.ticktok.client.support.ServerStub.TOKEN;

@TestInstance(PER_CLASS)
class TicktokTest {

    private final Ticktok ticktok = new Ticktok(options().domain(DOMAIN).token(TOKEN));

    private ServerStub server;

    @BeforeEach
    void init() throws Exception {
        server = new ServerStub(9999);
    }

    @Test
    void registerNewClock() {
        ticktok.schedule("kuku", "every.11.seconds", null);
        assertThat(server.lastClockRequest.getName(), is("kuku"));
        assertThat(server.lastClockRequest.getSchedule(), is("every.11.seconds"));
    }

    @Test
    void failOnInvalidSchedule() {
        assertThrows(TicktokException.class, () -> ticktok.schedule("kuku", "invalid", null));
    }

    @Test
    void invokeOnTick() throws Exception {
        CountDownLatch waitForTickLatch = new CountDownLatch(2);
        ticktok.schedule("kuku", "every.2.seconds", waitForTickLatch::countDown);
        ticktok.schedule("popo", "every.1.minute", waitForTickLatch::countDown);
        verifyCallbackWasntDoneSynchronicity(waitForTickLatch);
        server.tick("kuku");
        server.tick("popo");
        assertTimeoutPreemptively(ofSeconds(3), (Executable) waitForTickLatch::await);
        // pass
    }

    private void verifyCallbackWasntDoneSynchronicity(CountDownLatch countDownLatch) {
        assert countDownLatch.getCount() == 2;
    }

    @Test
    void disconnectClocks() throws Exception {
        AtomicInteger tickCount = new AtomicInteger();
        ticktok.schedule("ct-disconnect", "every.1.seconds", tickCount::incrementAndGet);
        ticktok.disconnect();
        server.tick("ct-disconnect");
        sleep(1000);
        assertThat("Disconnected clock got ticked", tickCount.get(), is(0));
    }

//    @Test
//    void tickOnClock() {
//        ticktok.schedule("popo", "every.2.seconds", () -> System.out.println("tick happened!"));
//        ticktok.tick("popo", "every.2.seconds");
//        assertTrue(server.latestTick().equals(""));
//    }

    @AfterEach
    void tearDown() throws Exception {
        ticktok.disconnect();
        server.stop();
    }
}