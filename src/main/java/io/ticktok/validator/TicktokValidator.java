package io.ticktok.validator;

import org.apache.commons.lang3.StringUtils;

import static io.ticktok.Ticktok.TicktokException;

public class TicktokValidator {

    public static void validate(String schedule) {
        if(StringUtils.isBlank(schedule)){
            throw new TicktokException("value cannot be empty");
        }
    }
}
