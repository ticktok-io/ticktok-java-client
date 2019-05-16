package io.ticktok.client.server.rest;

import io.ticktok.client.server.FailToCreateClockException;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;

import static org.testng.Assert.assertThrows;

public class RestResponseValidatorTest {

    @Test
    public void passGivenCreatedStatusCode(){
        new RestResponseValidator(responseWith(200)).validate( 200, new FailToCreateClockException("failed to create clock"));
        // pass
    }

    @Test
    public void failGivenBadRequestStatusCode() {
        assertThrows(FailToCreateClockException.class ,
                () -> new RestResponseValidator(responseWith(400)).validate( 200, new FailToCreateClockException("failed to create clock")));
    }

    private BasicHttpResponse responseWith(int statusCode) {
        return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("some protocol", 2, 3), statusCode, null));
    }

}