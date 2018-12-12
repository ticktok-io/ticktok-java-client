package io.ticktok.rest;

import com.google.gson.Gson;
import io.ticktok.Ticktok;
import io.ticktok.TicktokOptions;
import io.ticktok.register.Clock;
import io.ticktok.register.RegisterClockRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static io.ticktok.TicktokApi.REGISTER_NEW_CLOCK;

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
            HttpResponse httpResponse = Request.Post(calcUrl()).bodyString(handleBody(name, schedule), ContentType.APPLICATION_JSON).execute().returnResponse();
            validateResponse(httpResponse);
            return result(httpResponse);
        } catch (IOException e) {
            throw new Ticktok.TicktokException("fail to register clock");
        }
    }

    private String result(HttpResponse httpResponse) throws IOException {
        return EntityUtils.toString(httpResponse.getEntity());
    }

    private void validateResponse(HttpResponse httpResponse) {
        if (httpResponse.getStatusLine().getStatusCode() != 201){
            throw new Ticktok.TicktokServerException("fail to register clock duo to bad request : " + httpResponse.getStatusLine().getReasonPhrase());
        }
    }

    private String calcUrl() {
        return this.domain + "/" + REGISTER_NEW_CLOCK + "?access_token=" + this.token;
    }

    private String handleBody(String name, String schedule) {
        return new Gson().toJson(new RegisterClockRequest(name, schedule));
    }
}
