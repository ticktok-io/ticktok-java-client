package io.ticktok.client.server;

import io.ticktok.client.TicktokException;

public class FailToActOnClockException extends TicktokException {

    public FailToActOnClockException(String message) {
        super(message);
    }
}
