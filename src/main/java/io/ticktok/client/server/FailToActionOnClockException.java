package io.ticktok.client.server;

import io.ticktok.client.TicktokException;

public class FailToActionOnClockException extends TicktokException {

    public FailToActionOnClockException(String message) {
        super(message);
    }
}
