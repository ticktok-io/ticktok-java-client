package io.ticktok.client.server.rest;

import com.google.gson.Gson;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.server.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static io.ticktok.client.server.rest.TicktokMethods.*;

public class RestClockCreator {

    private final HttpClient httpClient = HttpClients.custom()
            .setRetryHandler(new StandardHttpRequestRetryHandler()).build();
    private final UrlResolver urlResolver;

    public RestClockCreator(TicktokOptions options) {
        this.urlResolver = new UrlResolver(options);
    }

    public Clock create(ClockRequest clockRequest) {
        try {
            final HttpPost httpPost = new HttpPost(urlResolver.resolve());
            httpPost.setEntity(new StringEntity(new Gson().toJson(clockRequest), ContentType.APPLICATION_JSON));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            validateResponse(httpResponse, CREATE);
            return clockFrom(httpResponse.getEntity());
        } catch (IOException e) {
            throw new ConnectionException("Connection error", e);
        }
    }

    //TODO: this isn't belong herem this class is all about creating a new clock
    // maybe you can create a ClockActionInvoker or something
    public void tick(ClockRequest clockRequest) {
        try {
            Clock clockBy = getClockBy(clockRequest);
            final HttpPut httpPut = new HttpPut(urlResolver.pathParam(clockBy.getId()).pathParam("tick").resolve());
            HttpResponse httpResponse = httpClient.execute(httpPut);
            validateResponse(httpResponse, ACTION);
        } catch (IOException e) {
            throw new ConnectionException("Connection error", e);
        }
    }

    private Clock getClockBy(ClockRequest clockRequest) {
        try {
            final HttpGet httpGet = new HttpGet(urlResolver.queryParam("name", clockRequest.getName()).queryParam("schedule", clockRequest.getSchedule()).resolve());
            HttpResponse httpResponse = httpClient.execute(httpGet);
            validateResponse(httpResponse, GET);
            return clockFrom(httpResponse.getEntity());
        } catch (IOException e) {
            //TODO: this is internal so no need for this catch clause
            throw new ConnectionException("Connection error", e);
        }
    }

    //TODO: this should be validate and be thrown specifically I think, no need for general here, not sure
    // even so it's better to have a map or factory than switch
    private void validateResponse(HttpResponse httpResponse, TicktokMethods expectedMethod) throws IOException {
        switch (expectedMethod){
            case CREATE:
                if(validateStatusCode(httpResponse, expectedMethod))
                    //TODO: you must always pass the original exception otherwise it's very hard to understand the cause
                    throw new FailToCreateClockException(EntityUtils.toString(httpResponse.getEntity()));
            case GET:
                if (validateStatusCode(httpResponse, expectedMethod))
                    throw new FailToGetClockException(EntityUtils.toString(httpResponse.getEntity()));
            case ACTION:
                if (validateStatusCode(httpResponse, expectedMethod))
                    throw new FailToActionOnClockException(EntityUtils.toString(httpResponse.getEntity()));
        }
    }

    private boolean validateStatusCode(HttpResponse httpResponse, TicktokMethods expectedMethod) {
        return httpResponse.getStatusLine().getStatusCode() != expectedMethod.status;
    }

    private Clock clockFrom(HttpEntity entity) throws IOException {
        return new Gson().fromJson(EntityUtils.toString(entity), Clock.class);
    }

}
