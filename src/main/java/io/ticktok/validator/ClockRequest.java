package io.ticktok.validator;

import org.apache.commons.lang3.StringUtils;

public class ClockRequest {

    private String name;
    private String schedule;

    private ClockRequest(String name, String schedule) {
        this.name = name;
        this.schedule = schedule;
    }

    public static ClockRequest create(String name, String schedule) {
        validateRequestParmValid(name, "name");
        validateRequestParmValid(schedule, "schedule");
        return new ClockRequest(name, schedule);
    }

    private static void validateRequestParmValid(String prop, String propName) {
        if(StringUtils.isBlank(prop)){
            throw new TicktokInvalidValueException( propName + " value cannot be empty");
        }
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
