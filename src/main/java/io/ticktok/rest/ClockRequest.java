package io.ticktok.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

@Slf4j
public class ClockRequest {

    private String name;
    private String schedule;

    private ClockRequest(String name, String schedule) {
        this.name = name;
        this.schedule = schedule;
    }

    public static ClockRequest create(String name, String schedule) {
        validateNotBlank(name, "name");
        validateNotBlank(schedule, "schedule");
        return new ClockRequest(name, schedule);
    }

    private static void validateNotBlank(String prop, String propName) {
        if(StringUtils.isBlank(prop)){
            String message = logException(propName);
            throw new TicktokInvalidValueException(message);
        }
    }

    private static String logException(String propName) {
        String message = MessageFormat.format("[{0}] parameter cannot be empty", propName);
        log.error(message);
        return message;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getName() {
        return name;
    }

    public static class TicktokInvalidValueException extends RuntimeException {
        public TicktokInvalidValueException(String message) {
            super(message);
        }
    }
}
