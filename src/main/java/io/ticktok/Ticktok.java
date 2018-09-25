package io.ticktok;

import io.ticktok.Listener.TickListener;
import io.ticktok.register.Clock;
import io.ticktok.rest.RestTicktokClient;

import java.io.IOException;

public class Ticktok {

    private TicktokOptions options;

    public Ticktok(TicktokOptions options){
        this.options = options;
    }

    public void newClock(String schedule, Runnable runnable) throws IOException {
        Clock clock = new RestTicktokClient(this.options).register(schedule);
        TickListener.listen(clock.getClockChannel(), runnable);
    }
}
