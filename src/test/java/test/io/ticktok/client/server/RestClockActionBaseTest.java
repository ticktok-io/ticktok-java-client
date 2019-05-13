package test.io.ticktok.client.server;

import io.ticktok.client.server.Clock;
import io.ticktok.client.server.FailToCreateClockException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.io.ticktok.client.server.support.TicktokServer;

import static org.junit.jupiter.api.Assertions.*;


//TODO: too many hirarchies, it's getting messy
class RestClockActionBaseTest extends ClockActionBaseTest {

    private TicktokServer server;

    @BeforeEach
    void setup(){
        server = new TicktokServer();
    }

    @Test
    void failOnBadRequest() {
        server.returnBadRequest();
        assertThrows(FailToCreateClockException.class, () -> createClockWith("my-pupu-clock","-"));
    }

    @Test
    void fireSingleTick() {
        Clock clock = createClockWith("my-clock", "@never");
        tick(clock);
        assertEquals(clock.getId(), server.latestTick());
    }

    @AfterEach
    void tearDown() {
        server.kill();
    }
}