package io.ticktok.client.server.rest;

import com.google.gson.Gson;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.server.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestClockActions {

    private final HttpClient httpClient = HttpClients.custom()
            .setRetryHandler(new StandardHttpRequestRetryHandler()).build();
    private final UrlResolver urlResolver;

    public RestClockActions(TicktokOptions options) {
        this.urlResolver = new UrlResolver(options);
    }

    public void tick(ClockRequest clockRequest) {
        try {
            Clock clockBy = getClockBy(clockRequest);
            final HttpPut httpPut = new HttpPut(urlResolver.pathParam(clockBy.getId()).pathParam("tick").resolve());
            HttpResponse httpResponse = httpClient.execute(httpPut);
            new RestResponseValidator(httpResponse).validate(204, new FailToActOnClockException("Failed to tick clock" + clockRequest.toString()));
        } catch (IOException e) {
            throw new ConnectionException("Connection error", e);
        }
    }

    private Clock getClockBy(ClockRequest clockRequest) throws IOException {
        final HttpGet httpGet = new HttpGet(urlResolver.queryParam("name", clockRequest.getName()).queryParam("schedule", clockRequest.getSchedule()).resolve());
        HttpResponse httpResponse = httpClient.execute(httpGet);
        String entity = extractEntityFrom(httpResponse);
        new RestResponseValidator(httpResponse).validate(200, new FailToGetClockException(entity));
        return clockFrom(entity);
    }

    private String extractEntityFrom(HttpResponse httpResponse) throws IOException {
        return EntityUtils.toString(httpResponse.getEntity());
    }

    private Clock clockFrom(String entity) {
        return new Gson().fromJson(entity, Clock.class);
    }
}
