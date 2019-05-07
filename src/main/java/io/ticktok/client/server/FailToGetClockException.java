package io.ticktok.client.server;

import io.ticktok.client.TicktokException;

public class FailToGetClockException extends TicktokException {

    public FailToGetClockException (String message) {
        super(message);
    }
}
