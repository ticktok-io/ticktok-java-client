package io.ticktok;

import io.ticktok.Listener.TickListener;
import io.ticktok.register.Clock;
import io.ticktok.rest.RestTicktokClient;
import io.ticktok.rest.ClockRequest;

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
        TickListener.listen(clock.getClockChannel(), runnable);
    }

    public static class TicktokException extends RuntimeException {
        public TicktokException(String message) {
            super(message);
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
}
