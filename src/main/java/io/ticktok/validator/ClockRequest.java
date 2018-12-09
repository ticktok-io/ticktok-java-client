package io.ticktok.validator;

import org.apache.commons.lang3.StringUtils;

import static io.ticktok.Ticktok.TicktokException;

public class ClockRequest {

    private String name;
    private String schedule;

    public ClockRequest(String name, String schedule) {
        this.name = validate(name);
        this.schedule = validate(schedule);
    }

    private String validate(String prop) {
        if(StringUtils.isBlank(prop)){
            throw new TicktokException("value cannot be empty");
        }

        return prop;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getName() {
        return name;
    }
}
