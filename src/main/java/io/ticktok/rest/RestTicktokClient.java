package io.ticktok.rest;

import com.google.gson.Gson;
import io.ticktok.Ticktok;
import io.ticktok.TicktokOptions;
import io.ticktok.register.Clock;
import io.ticktok.register.RegisterClockRequest;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;

import static io.ticktok.TicktokApi.*;

public class RestTicktokClient {
    private final String token;
    private final String domain;

    public RestTicktokClient(TicktokOptions options){
        this.domain = options.getDomain();
        this.token = options.getToken();
    }

    public Clock register(ClockRequest clockRequest) {
        return new Gson().fromJson(call(clockRequest.getName(), clockRequest.getSchedule()), Clock.class);
    }

    private String call(String name, String schedule) {
        try {
            return Request.
                    Post(calcUrl()).
                    bodyString(handleBody(name, schedule), ContentType.APPLICATION_JSON).
                    execute().
                    returnContent().
                    asString();
        } catch (IOException e) {
            throw new Ticktok.TicktokException("fail to register clock");
        }
    }

    private String calcUrl() {
        return this.domain + "/" + REGISTER_NEW_CLOCK + "?access_token=" + this.token;
    }

    private String handleBody(String name, String schedule) {
        return new Gson().toJson(new RegisterClockRequest(name, schedule));
    }
}
