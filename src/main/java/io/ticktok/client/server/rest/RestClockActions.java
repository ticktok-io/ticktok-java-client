package io.ticktok.client.server.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;

import static java.lang.String.format;

public class RestClockActions {

    private final HttpClient httpClient = HttpClients.custom()
            .setRetryHandler(new StandardHttpRequestRetryHandler()).build();
    private final ClocksUrlResolver urlResolver;

    public RestClockActions(TicktokOptions options) {
        this.urlResolver = new ClocksUrlResolver(options);
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
        return firstClockFrom(extractEntityFrom(httpResponse), clockRequest.getName(), clockRequest.getSchedule());
    }

    private Clock firstClockFrom(String entity, String name, String schedule) {
        final List<Clock> clocks = new Gson().fromJson(entity, new TypeToken<List<Clock>>() {}.getType());
        if (clocks.isEmpty()) {
            throw new ClockNotFoundException(format(
                    "Clock named: %s, schedule: %s isn't currently configured", name, schedule));
        }
        return clocks.get(0);
    }

    private String extractEntityFrom(HttpResponse httpResponse) throws IOException {
        return EntityUtils.toString(httpResponse.getEntity());
    }

}
