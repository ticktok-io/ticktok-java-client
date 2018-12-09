package io.ticktok.validator;

import io.ticktok.Ticktok;
import org.junit.Test;

public class ClockRequestTest {

    @Test(expected = Ticktok.TicktokException.class)
    public void shouldThrowExceptionGivenEmptyString(){ new ClockRequest("", "");
    }

    @Test(expected = Ticktok.TicktokException.class)
    public void shouldThrowExceptionGivenNull(){
        new ClockRequest(null, null);
    }

}