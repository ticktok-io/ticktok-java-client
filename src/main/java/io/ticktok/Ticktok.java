package io.ticktok;

import io.ticktok.Listener.TickListener;
import io.ticktok.register.Clock;
import io.ticktok.rest.RestTicktokClient;

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
        try {
            Clock clock = new RestTicktokClient(this.options).register(this.name, this.schedule);
            TickListener.listen(clock.getClockChannel(), runnable);
        } catch (Exception e) {
            throw new TicktokException("operation failed: " + e);
        }
    }

    public static class TicktokException extends RuntimeException {
        public TicktokException(String message) {
            super(message);
        }
    }
}
