package io.ticktok.rest;

import io.ticktok.rest.ClockRequest;
import io.ticktok.rest.ClockRequest.TicktokInvalidValueException;
import org.junit.jupiter.api.Test;

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
        assertThrows(TicktokInvalidValueException.class, () ->
                ClockRequest.create("myName", ""), "schedule value cannot be empty");
    }
}