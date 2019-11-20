package io.ticktok.client.server.rest;

import io.ticktok.client.TicktokException;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestResponseValidator {

    private final HttpResponse httpResponse;

    public RestResponseValidator(HttpResponse httpResponse){
        this.httpResponse = httpResponse;
    }

    public void created(TicktokException exception) throws IOException {
        if(httpResponse.getStatusLine().getStatusCode() != 201){
            throwError(exception);
        }
    }

    private void throwError(TicktokException exception) throws IOException {
        EntityUtils.consume(httpResponse.getEntity());
        throw exception;
    }

    public void ok(TicktokException exception) throws IOException {
        if(httpResponse.getStatusLine().getStatusCode() >= 300){
            throwError(exception);
        }
    }

}
