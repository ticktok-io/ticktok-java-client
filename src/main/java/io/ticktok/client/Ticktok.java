package io.ticktok.client;

import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.RestClockCreator;
import io.ticktok.client.tick.TickConsumer;
import io.ticktok.client.tick.TickListener;

public class Ticktok {

    private final TickListener tickListener = new TickListener();
    private TicktokOptions options;

    public Ticktok(TicktokOptions options) {
        this.options = options;
    }

    public static TicktokOptions options() {
        return new TicktokOptions();
    }

    public void schedule(String name, String schedule, TickConsumer consumer) {
        Clock clock = new RestClockCreator(this.options).create(new ClockRequest(name, schedule));
        tickListener.listen(clock.getChannel(), consumer);
    }

    public void disconnect() {
       tickListener.disconnect();
    }
}
