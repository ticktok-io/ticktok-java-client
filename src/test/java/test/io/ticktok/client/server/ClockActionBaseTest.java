package test.io.ticktok.client.server;

import io.ticktok.client.Ticktok;
import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.rest.RestClockActions;
import io.ticktok.client.server.rest.RestClockCreator;

public abstract class ClockActionBaseTest {

    private static final String DOMAIN = "http://localhost:" + 1212;
    private static final String TOKEN = "my_token";

    Clock createClockWith(String name, String schedule) {
        return new RestClockCreator(Ticktok.options().domain(DOMAIN).token(TOKEN))
                .create(clockRequest(name, schedule));
    }

    private ClockRequest clockRequest(String name, String schedule) {
        return new ClockRequest(name, schedule);
    }

    void tick(Clock clock) {
        new RestClockActions(Ticktok.options().domain(DOMAIN).token(TOKEN)).
                tick(clockRequest(clock.getName(), clock.getSchedule()));
    }
}
