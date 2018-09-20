package io.ticktok;

import com.google.gson.Gson;
import io.ticktok.handler.TickHandler;
import io.ticktok.register.Clock;
import io.ticktok.register.RegisterClockRequest;
import io.ticktok.rest.TicktokRestClient;

import java.io.IOException;

public class Ticktok {

    private TicktokOptions options;

    public Ticktok(TicktokOptions options){
        this.options = options;
    }

    public Clock newClock(String schedule, Runnable runnable) throws IOException {
        TicktokRestClient client = new TicktokRestClient(this.options);
        runnable.run();
        return new Gson().fromJson(client.post(handleBody(schedule), TicktokApi.REGISTER_NEW_CLOCK), Clock.class);
    }

    private String handleBody(String schedule) {
        return new Gson().toJson(new RegisterClockRequest(schedule));
    }
}
