package io.ticktok;

import com.google.gson.Gson;
import io.ticktok.register.Clock;
import io.ticktok.register.RegisterClockRequest;
import io.ticktok.support.TicktockServiceStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TicktokTest {

    private static final String TICKTOK_SERVICE_DOMAIN = "http://localhost:9999";
    private static final String EVERY_5_SECONDS = "every.5.seconds";
    private static final String TOKEN = "Bfmx3Z7y9GxY4yLrKP";

    @Before
    public void init() throws IOException {
        new TicktockServiceStub(9999);
    }

    @Test
    public void shouldRegisterNewClock() throws IOException {
        Clock clock = new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS);
        assertThat(clock.getSchedule(), is(EVERY_5_SECONDS));
    }

//    @Test
//    public void shouldTickEvery5Sec() throws IOException {
//        new Ticktok(new TicktokOptions(TICKTOK_SERVICE_DOMAIN, TOKEN)).newClock(EVERY_5_SECONDS);
//    }

}