package io.ticktok.validator;

import io.ticktok.Ticktok;
import org.junit.Test;

public class TicktokValidatorTest {

    @Test(expected = Ticktok.TicktokException.class)
    public void shouldThrowExceptionGivenEmptyString(){
        TicktokValidator.validate("");
    }

    @Test(expected = Ticktok.TicktokException.class)
    public void shouldThrowExceptionGivenNull(){
        TicktokValidator.validate(null);
    }

}