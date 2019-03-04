package io.ticktok.client.rest;

import com.google.gson.Gson;
import io.ticktok.client.TicktokException;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.TicktokServerException;
import io.ticktok.client.register.Clock;
import io.ticktok.client.register.RegisterClockRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.MessageFormat;

@Slf4j
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
            log.debug("registered clock for name: {} for every : {} ", name, schedule);
            return result(httpResponse);
        } catch (IOException e) {
            String message = "fail to register clock, please follow reason:" + ExceptionUtils.getStackTrace(e);
            log.error(message);
            throw new TicktokException(message);
        }
    }

    private String result(HttpResponse httpResponse) {
        return logErrorEntity(httpResponse);
    }

    private void validateResponse(HttpResponse httpResponse) {
        if (httpResponse.getStatusLine().getStatusCode() != 201){
            String message = logException(httpResponse);
            throw new TicktokServerException(message);
        }
    }

    private String logException(HttpResponse httpResponse) {
        String message = MessageFormat.format("fail to register clock duo to bad request : {0} - {1}", httpResponse.getStatusLine().getReasonPhrase(), logErrorEntity(httpResponse));
        log.error(message);
        return message;
    }

    private String logErrorEntity(HttpResponse httpResponse) {
        try {
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            // was no body in error, do nothing
        }

        return "";
    }

    private String calcUrl() {
        return this.domain + "/" + REGISTER_NEW_CLOCK + "?access_token=" + this.token;
    }

    private String handleBody(String name, String schedule) {
        return new Gson().toJson(new RegisterClockRequest(name, schedule));
    }
}
