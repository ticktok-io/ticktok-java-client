package io.ticktok;

import io.ticktok.Listener.TickListener;
import io.ticktok.register.Clock;
import io.ticktok.rest.RestTicktokClient;

public class Ticktok {

    private TicktokOptions options;

    public Ticktok(TicktokOptions options){
        this.options = options;
    }

    public void newClock(String schedule, Runnable runnable) {
        try {
            Clock clock = new RestTicktokClient(this.options).register(schedule);
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
