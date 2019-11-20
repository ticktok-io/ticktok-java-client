package io.ticktok.client.server.rest;

import com.google.gson.Gson;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.server.Clock;
import io.ticktok.client.server.ClockRequest;
import io.ticktok.client.server.ConnectionException;
import io.ticktok.client.server.FailToCreateClockException;
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

    private final HttpClient httpClient = HttpClients.custom()
            .setRetryHandler(new StandardHttpRequestRetryHandler()).build();
    private final ClocksUrlResolver urlResolver;

    public RestClockCreator(TicktokOptions options) {
        this.urlResolver = new ClocksUrlResolver(options);
    }

    public Clock create(ClockRequest clockRequest) {
        try {
            final HttpPost httpPost = new HttpPost(urlResolver.resolve());
            httpPost.setEntity(new StringEntity(new Gson().toJson(clockRequest), ContentType.APPLICATION_JSON));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String entity = extractEntityFrom(httpResponse);
            validateResponse(httpResponse, entity);
            return clockFrom(entity);
        } catch (IOException e) {
            throw new ConnectionException("Connection error", e);
        }
    }

    private String extractEntityFrom(HttpResponse httpResponse) throws IOException {
        return EntityUtils.toString(httpResponse.getEntity());
    }

    private void validateResponse(HttpResponse httpResponse, String entity) throws IOException {
        new RestResponseValidator(httpResponse).created(new FailToCreateClockException(entity));
    }

    private Clock clockFrom(String entity){
        return new Gson().fromJson(entity, Clock.class);
    }

}
