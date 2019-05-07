package io.ticktok.client.server;

import io.ticktok.client.TicktokException;

public class FailToCreateClockException extends TicktokException {

    public FailToCreateClockException(String message) {
        super(message);
    }
}
