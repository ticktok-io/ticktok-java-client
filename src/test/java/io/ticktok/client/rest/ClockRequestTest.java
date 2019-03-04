package io.ticktok.client.rest;

import io.ticktok.client.rest.ClockRequest.TicktokInvalidValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClockRequestTest {

    @Test
    void shouldThrowExceptionGivenEmptyString(){
        assertThrows(TicktokInvalidValueException.class, () ->
                ClockRequest.create("", ""));
    }

    @Test
    void shouldThrowExceptionGivenNull(){
        assertThrows(TicktokInvalidValueException.class, () ->
                ClockRequest.create(null, null));
    }

    @Test
    void exceptionMsgShouldBeCorrelatedToCorruptedValue() {
        TicktokInvalidValueException exception = assertThrows(TicktokInvalidValueException.class, () ->
                ClockRequest.create("myName", ""));
        assertEquals(exception.getMessage(), "[schedule] parameter cannot be empty");
    }
}