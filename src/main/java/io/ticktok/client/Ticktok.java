package io.ticktok.client;

import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.rest.RestClockCreator;
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
        //TODO: now that I see it I think clockRequest should be visible to the rest layer scope and not here
        Clock clock = new RestClockCreator(options).create(new ClockRequest(name, schedule));
        tickListener.forChannel(clock.getChannel()).register(consumer);
    }

    //TODO: no test for this function
    public void tick(String name, String schedule) {
        new RestClockCreator(options).tick(new ClockRequest(name, schedule));
    }

    public void disconnect() {
       tickListener.disconnect();
    }
}
