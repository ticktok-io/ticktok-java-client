package io.ticktok.rest;

import io.ticktok.rest.ClockRequest.TicktokInvalidValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClockRequestTest {

    @Test
    public void shouldThrowExceptionGivenEmptyString(){
        assertThrows(TicktokInvalidValueException.class, () ->
                ClockRequest.create("", ""));
    }

    @Test
    public void shouldThrowExceptionGivenNull(){
        assertThrows(TicktokInvalidValueException.class, () ->
                ClockRequest.create(null, null));
    }

    @Test
    public void exceptionMsgShouldBeCorrelatedToCorruptedValue() {
        TicktokInvalidValueException exception = assertThrows(TicktokInvalidValueException.class, () ->
                ClockRequest.create("myName", ""));
        assertEquals(exception.getMessage(), "[schedule] parameter cannot be empty");
    }
}