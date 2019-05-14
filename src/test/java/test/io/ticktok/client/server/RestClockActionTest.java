package test.io.ticktok.client.server;

import io.ticktok.client.Ticktok;
import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.FailToCreateClockException;
import io.ticktok.client.server.rest.RestClockActions;
import io.ticktok.client.server.rest.RestClockCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.io.ticktok.client.server.support.TicktokServer;

import static org.junit.jupiter.api.Assertions.*;


class RestClockActionTest {

    private TicktokServer server;
    private static final String DOMAIN = "http://localhost:" + 1212;
    private static final String TOKEN = "my_token";

    @BeforeEach
    void setup(){
        server = new TicktokServer();
    }

    @Test
    void failOnBadRequest() {
        server.returnBadRequest();
        assertThrows(FailToCreateClockException.class, () -> createClockWith("my-pupu-clock","-"));
    }

    private Clock createClockWith(String name, String schedule) {
        return new RestClockCreator(Ticktok.options().domain(DOMAIN).token(TOKEN))
                .create(clockRequest(name, schedule));
    }

    @Test
    void fireSingleTick() {
        Clock clock = createClockWith("my-clock", "@never");
        tick(clock);
        assertEquals(clock.getId(), server.latestTick());
    }

    private void tick(Clock clock) {
        new RestClockActions(Ticktok.options().domain(DOMAIN).token(TOKEN)).
                tick(clockRequest(clock.getName(), clock.getSchedule()));
    }

    private ClockRequest clockRequest(String name, String schedule) {
        return new ClockRequest(name, schedule);
    }

    @AfterEach
    void tearDown() {
        server.kill();
    }
}