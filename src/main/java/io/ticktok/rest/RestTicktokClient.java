package io.ticktok.rest;

import com.google.gson.Gson;
import io.ticktok.TicktokApi;
import io.ticktok.TicktokOptions;
import io.ticktok.register.Clock;
import io.ticktok.register.RegisterClockRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestTicktokClient {
    private final String token;
    private HttpClient client;
    private final String domain;

    public RestTicktokClient(TicktokOptions options){
        this.domain = options.getDomain();
        this.token = options.getToken();
        this.client = HttpClientBuilder.create().build();
    }

    public String post(String entry, String entryDomain) throws IOException {
        return handleBodyCall(new HttpPost(this.domain + "/" + entryDomain), entry);
    }

    private String handleBodyCall(HttpEntityEnclosingRequestBase request, String entry) throws IOException {
        request.setEntity(new StringEntity(entry));
        request.setHeaders(defaultHeaders());
        HttpResponse response = client.execute(request);
        String restCallResponse = EntityUtils.toString(response.getEntity());
        request.releaseConnection();
        validateResponseWasOk(response);
        return restCallResponse;
    }

    private BasicHeader[] defaultHeaders() {
        return new BasicHeader[]{
                new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
                new BasicHeader(HttpHeaders.ACCEPT, "application/json"),
                new BasicHeader(HttpHeaders.AUTHORIZATION, this.token)
        };
    }

    private void validateResponseWasOk(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            System.out.println("response code not ok " + response.getStatusLine().getStatusCode());
        }
    }

    public Clock register(String name, String schedule) throws IOException {
        return new Gson().fromJson(post(handleBody(name, schedule), TicktokApi.REGISTER_NEW_CLOCK), Clock.class);
    }

    private String handleBody(String name, String schedule) {
        return new Gson().toJson(new RegisterClockRequest(name, schedule));
    }
}
