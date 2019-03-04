package io.ticktok.client.rest;

import com.google.gson.Gson;
import io.ticktok.client.TicktokException;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.TicktokServerException;
import io.ticktok.client.register.Clock;
import io.ticktok.client.register.RegisterClockRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestTicktokClient {
    public static final String REGISTER_NEW_CLOCK = "api/v1/clocks";

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
            throw new TicktokException("fail to register clock");
        }
    }

    private String result(HttpResponse httpResponse) throws IOException {
        return EntityUtils.toString(httpResponse.getEntity());
    }

    private void validateResponse(HttpResponse httpResponse) {
        if (httpResponse.getStatusLine().getStatusCode() != 201){
            throw new TicktokServerException("fail to register clock duo to bad request : " + httpResponse.getStatusLine().getReasonPhrase());
        }
    }

    private String calcUrl() {
        return this.domain + "/" + REGISTER_NEW_CLOCK + "?access_token=" + this.token;
    }

    private String handleBody(String name, String schedule) {
        return new Gson().toJson(new RegisterClockRequest(name, schedule));
    }
}
