package io.ticktok.client;

import io.ticktok.client.tick.TickConsumer;
import io.ticktok.client.tick.TickListener;
import io.ticktok.client.register.Clock;
import io.ticktok.client.server.RestClockCreator;
import io.ticktok.client.server.ClockRequest;

public class Ticktok {

    private TicktokOptions options;
    private String name;
    private String schedule;

    public Ticktok(TicktokOptions options) {
        this.options = options;
    }

    public Ticktok newClock(String name) {
        this.name = name;
        return this;
    }

    public Ticktok on(String schedule) {
        this.schedule = schedule;
        return this;
    }

    public void invoke(Runnable runnable) {
//        Clock clock = new RestClockCreator(this.options).create(ClockRequest.create(this.name, this.schedule));
    }

    public static TicktokOptions options() {
        return new TicktokOptions();
    }

    public static ClockDetails clock() {
        return new ClockDetails();
    }

    public void schedule(String name, String schedule, TickConsumer consumer) {
        Clock clock = new RestClockCreator(this.options).create(new ClockRequest(name, schedule));
        new TickListener().listen(clock.getChannel(), consumer);
    }

}
