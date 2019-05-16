package io.ticktok.client.server;

import io.ticktok.client.TicktokException;

public class ClockNotFoundException extends TicktokException {

    public ClockNotFoundException(String message) {
        super(message);
    }
}
