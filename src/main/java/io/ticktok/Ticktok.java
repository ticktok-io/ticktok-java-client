package io.ticktok;

import com.google.gson.Gson;
import io.ticktok.register.RegisterClockRequest;
import io.ticktok.rest.TicktokRestClient;

import java.io.IOException;

public class Ticktok {

    private TicktokOptions options;

    public Ticktok(TicktokOptions options){
        this.options = options;
    }

    public void newClock(String schedule) throws IOException {
        new TicktokRestClient(this.options).post(handleBody(schedule), TicktokApi.REGISTER_NEW_CLOCK);
    }

    private String handleBody(String schedule) {
        return new Gson().toJson(new RegisterClockRequest(schedule));
    }
}
