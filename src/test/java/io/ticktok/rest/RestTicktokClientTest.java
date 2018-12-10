package io.ticktok.rest;

import io.ticktok.Ticktok;
import io.ticktok.TicktokOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestTicktokClientTest {

    @Test
    public void shouldFailOnServiceNotAvailable(){
        assertThrows(Ticktok.TicktokException.class, () ->
                new RestTicktokClient(new TicktokOptions()).register(ClockRequest.create("clock_name", "clock_schedule")));
    }

    @Test
    public void failMsgShouldBeRespectable(){
        assertThrows(Ticktok.TicktokException.class, () ->
                new RestTicktokClient(new TicktokOptions()).register(ClockRequest.create("clock_name", "clock_schedule")), "fail to register clock");
    }
}