package io.ticktok.validator;

import io.ticktok.validator.ClockRequest.TicktokInvalidValueException;
import org.junit.Test;

public class ClockRequestTest {

    @Test(expected = TicktokInvalidValueException.class)
    public void shouldThrowExceptionGivenEmptyString(){
        ClockRequest.create("", "");
    }

    @Test(expected = TicktokInvalidValueException.class)
    public void shouldThrowExceptionGivenNull(){
        ClockRequest.create(null, null);
    }

}