package io.ticktok;

import com.google.gson.Gson;
import io.ticktok.register.RegisterClockRequest;
import io.ticktok.rest.TicktokRestClient;

import java.io.IOException;

public class TicktokClient{

    private String token;

    public TicktokClient(String token){
        this.token = token;
    }

    public void registerClock(String schedule, String consumerId) throws IOException {
        new TicktokRestClient("http://localhost:9999").persistEntry(handleRequestBody(schedule, consumerId), "api/v1/clocks");
    }

    private String handleRequestBody(String schedule, String consumerId) {
        return new Gson().toJson(new RegisterClockRequest(consumerId, schedule));
    }
}
