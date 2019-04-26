package test.io.ticktok.client.tick;

import com.google.gson.JsonObject;
import io.ticktok.client.tick.TickChannel;
import io.ticktok.client.tick.TickConsumer;
import io.ticktok.client.tick.TickListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.rockm.blink.BlinkServer;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import static test.io.ticktok.client.tick.HttpTickerTest.TicktokServer.PORT;

public class HttpTickerTest {

    public static final int SECOND = 1000;

    final TickListener tickListener = new TickListener();
    TicktokServer server;

    @Test
    void ignoreOnConnectionError() throws Exception {
        // No server
        assertNoTicksWereConsumed();
    }

    private void assertNoTicksWereConsumed() throws InterruptedException {
        AtomicInteger tickCount = new AtomicInteger();
        register("no-ticks", tickCount::incrementAndGet);
        assertNoTicks(tickCount);
    }

    private void assertNoTicks(AtomicInteger tickCount) throws InterruptedException {
        sleep(SECOND);
        assertThat("Ticks were consumed", tickCount.get(), is(0));
    }

    private void register(String id, TickConsumer consumer) {
        tickListener.forChannel(httpChannel(id)).register(consumer);
    }

    private TickChannel httpChannel(String id) {
        Map<String, String> details = new HashMap<>();
        details.put("url", format("http://localhost:%s/%s/pop", PORT, id));
        return TickChannel.builder().type("http").details(details).build();
    }

    @Test
    void ignoreOnRequestError() throws Exception {
        server = new TicktokServer().onPopFailWith(500);
        assertNoTicksWereConsumed();
        assertTrue(server.popped);
    }

    @Test
    void invokeOnTick() throws Exception {
        server = new TicktokServer().tickOn("tick-me");
        PacedTickCounter tickCounter = new PacedTickCounter(2);
        register("tick-me", tickCounter::countDown);
        tickCounter.assertNoTicks();
        assertTimeoutPreemptively(ofSeconds(2), tickCounter::await, "No ticks");
    }

    @Test
    void ignoreWhenThereNoTicksToPop() throws Exception {
        server = new TicktokServer();
        assertNoTicksWereConsumed();
    }

    @Test
    void disconnectAllClocks() throws Exception {
        server = new TicktokServer();
        AtomicInteger tickCount = new AtomicInteger();
        register("c1", tickCount::incrementAndGet);
        register("c2", tickCount::incrementAndGet);
        tickListener.disconnect();
        sleep(100);
        server.tickOn("c1", "c2");
        assertNoTicks(tickCount);
    }

    @Test
    void allowToRegisterAfterDisconnect() throws Exception {
        server = new TicktokServer().tickOn("c1");
        tickListener.disconnect();
        CountDownLatch tickConsumed = new CountDownLatch(1);
        register("c1", tickConsumed::countDown);
        assertTimeoutPreemptively(ofSeconds(1), (Executable) tickConsumed::await);
    }

    @Test
    void replaceCallbackForAGivenClock() throws Exception {
        server = new TicktokServer();
        CountDownLatch countForOriginal = new CountDownLatch(1);
        CountDownLatch countForNew = new CountDownLatch(1);
        register("c1", countForOriginal::countDown);
        register("c1", countForNew::countDown);
        server.tickOn("c1");
        assertTimeoutPreemptively(ofSeconds(1), (Executable) countForNew::await);
        assertThat("original consumer should not get ticks", countForOriginal.getCount(), is(1L));
    }

    @AfterEach
    void stopServer() {
        tickListener.disconnect();
        if (server != null) {
            server.stop();
        }
    }

    static class TicktokServer extends BlinkServer {

        public static final int PORT = 9191;

        public boolean popped;
        private int statusCode = 200;
        private Set<String> clocks = new HashSet<>();

        public TicktokServer() throws IOException {
            super(PORT);
            get("/{id}/pop", (req, res) -> {
                popped = true;
                res.status(statusCode);
                if (clocks.contains(req.pathParam("id"))) {
                    return asList(new JsonObject());
                }
                return new ArrayList<>();
            });
        }

        public TicktokServer tickOn(String... ids) {
            this.clocks.addAll(asList(ids));
            return this;
        }

        public TicktokServer onPopFailWith(int code) {
            statusCode = code;
            return this;
        }
    }

    private class PacedTickCounter {
        private static final long PACE = 500;
        private final int numOfTicks;
        private CountDownLatch tickCounter;
        private long lastTickTime = 0;

        public PacedTickCounter(int numOfTicks) {
            this.numOfTicks = numOfTicks;
            tickCounter = new CountDownLatch(numOfTicks);
        }

        public void countDown() {
            final long timeMillis = System.currentTimeMillis();
            if (timeMillis > lastTickTime + PACE) {
                tickCounter.countDown();
                lastTickTime = timeMillis;
            } else {
                fail("Ticks aren't paced");
            }
        }

        public void assertNoTicks() {
            assert tickCounter.getCount() == numOfTicks;
        }

        public void await() throws InterruptedException {
            tickCounter.await();
        }
    }
}
