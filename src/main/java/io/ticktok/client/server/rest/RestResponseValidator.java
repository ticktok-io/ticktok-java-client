package io.ticktok.client.server.rest;

import io.ticktok.client.TicktokException;
import org.apache.http.HttpResponse;

public class RestResponseValidator {

    private final HttpResponse httpResponse;

    public RestResponseValidator(HttpResponse httpResponse){
        this.httpResponse = httpResponse;
    }

    public void validate(int expectedStatus, TicktokException exception) {
        if(httpResponse.getStatusLine().getStatusCode() != expectedStatus){
            throw exception;
        }
    }
}
