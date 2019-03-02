package io.ticktok.client;

import io.ticktok.client.listener.TickListener;
import io.ticktok.client.register.Clock;
import io.ticktok.client.rest.RestTicktokClient;
import io.ticktok.client.rest.ClockRequest;

public class Ticktok {

    private TicktokOptions options;
    private String name;
    private String schedule;

    public Ticktok(TicktokOptions options){
        this.options = options;
    }

    public Ticktok newClock(String name) {
        this.name = name;
        return this;
    }

    public Ticktok on(String schedule){
        this.schedule = schedule;
        return this;
    }

    public void invoke(Runnable runnable){
        Clock clock = new RestTicktokClient(this.options).register(ClockRequest.create(this.name, this.schedule));
        TickListener.listen(clock.getChannel(), runnable);
    }

}
