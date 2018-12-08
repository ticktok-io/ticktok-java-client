package io.ticktok;

import io.ticktok.Listener.TickListener;
import io.ticktok.register.Clock;
import io.ticktok.rest.RestTicktokClient;

import static io.ticktok.validator.TicktokValidator.validate;

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
        validate(this.name);
        validate(this.schedule);
        Clock clock = new RestTicktokClient(this.options).register(this.name, this.schedule);
        TickListener.listen(clock.getClockChannel(), runnable);
    }

    public static class TicktokException extends RuntimeException {
        public TicktokException(String message) {
            super(message);
        }
    }
}
