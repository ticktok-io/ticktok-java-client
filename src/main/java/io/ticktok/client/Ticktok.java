package io.ticktok.client;

import io.ticktok.client.tick.TickConsumer;
import io.ticktok.client.tick.TickListener;
import io.ticktok.client.register.Clock;
import io.ticktok.client.server.RestClockCreator;
import io.ticktok.client.server.ClockRequest;

public class Ticktok {

    private TicktokOptions options;

    public Ticktok(TicktokOptions options) {
        this.options = options;
    }

    public static TicktokOptions options() {
        return new TicktokOptions();
    }

    public void schedule(String name, String schedule, TickConsumer consumer) {
        Clock clock = new RestClockCreator(this.options).create(new ClockRequest(name, schedule));
        new TickListener().listen(clock.getChannel(), consumer);
    }

}
