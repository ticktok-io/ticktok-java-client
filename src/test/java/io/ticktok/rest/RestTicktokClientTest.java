package io.ticktok.rest;

import io.ticktok.Ticktok;
import io.ticktok.TicktokOptions;
import io.ticktok.validator.ClockRequest;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class RestTicktokClientTest {

    @Test(expected = Ticktok.TicktokException.class)
    public void shouldFailOnServiceNotAvailable(){
        new RestTicktokClient(new TicktokOptions()).register(ClockRequest.create("clock_name", "clock_schedule"));
    }

    @Test
    public void failMsgShouldBeRespectable(){
        try {
            new RestTicktokClient(new TicktokOptions()).register(ClockRequest.create("clock_name", "clock_schedule"));
        } catch (Ticktok.TicktokException e){
            Assert.assertThat(e.getMessage(), Is.is("fail to register clock"));
        }

    }
}