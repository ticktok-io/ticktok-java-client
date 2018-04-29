package io.ticktok;

import com.google.gson.Gson;
import io.ticktok.register.RegisterClockRequest;
import io.ticktok.support.TicktockServiceStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TicktokTest {

    private static final String TICKTOK_SERVICE_DOMAIN = "http://localhost:9999";
    public static final String EVERY_5_SECONDS = "every.5.seconds";
    private TicktockServiceStub ticktockServiceStub;
    private static final String TOKEN = "Bfmx3Z7y9GxY4yLrKP";

    @Before
    public void init() throws IOException {
        this.ticktockServiceStub =  new TicktockServiceStub(9999);
    }

    @Test
    public void shouldRegisterNewClock() throws IOException {
        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS);
        assertThat(ticktockServiceStub.getToken(), is(TOKEN));
        assertThat(ticktockServiceStub.getBody(), is(expectedBody()));
    }

    private String expectedBody() {
        return new Gson().toJson(new RegisterClockRequest("every.5.seconds"));
    }

}