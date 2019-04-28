package io.ticktok.client.server.rest;

import com.google.gson.Gson;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.ConnectionException;
import io.ticktok.client.server.FailToCreateClockException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestClockCreator {
    public static final String REGISTER_NEW_CLOCK = "api/v1/clocks";

    private final HttpClient httpClient = HttpClients.custom()
            .setRetryHandler(new StandardHttpRequestRetryHandler()).build();
    private final String token;
    private final String domain;

    public RestClockCreator(TicktokOptions options) {
        this.domain = options.getDomain();
        this.token = options.getToken();
    }

    public Clock create(ClockRequest clockRequest) {
        try {
            final HttpPost httpPost = new HttpPost(createClockUrl());
            httpPost.setEntity(new StringEntity(new Gson().toJson(clockRequest), ContentType.APPLICATION_JSON));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            validateResponse(httpResponse);
            return clockFrom(httpResponse.getEntity());
        } catch (IOException e) {
            throw new ConnectionException("Connection error", e);
        }
    }

    private String createClockUrl() {
        return this.domain + "/" + REGISTER_NEW_CLOCK + "?access_token=" + this.token;
    }

    private void validateResponse(HttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusLine().getStatusCode() != 201) {
            throw new FailToCreateClockException(EntityUtils.toString(httpResponse.getEntity()));
        }
    }

    private Clock clockFrom(HttpEntity entity) throws IOException {
        return new Gson().fromJson(EntityUtils.toString(entity), Clock.class);
    }

}
