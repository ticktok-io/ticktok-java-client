package io.ticktok.rest;

import io.ticktok.TicktokOptions;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.io.IOException;

public class TicktokRestClient {
    private final String token;
    private HttpClient client;
    private final String domain;

    public TicktokRestClient(TicktokOptions options){
        this.domain = options.getDomain();
        this.token = options.getToken();
        this.client = HttpClientBuilder.create().build();
    }

    private void handleBodyCall(HttpEntityEnclosingRequestBase request, String entry) throws IOException {
        request.setEntity(new StringEntity(entry));
        request.setHeaders(defaultHeaders());
        HttpResponse response = client.execute(request);
        request.releaseConnection();
        validateResponseWasOk(response);
    }

    public void post(String entry, String entryDomain) throws IOException {
        handleBodyCall(new HttpPost(this.domain + "/" + entryDomain), entry);
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

}
