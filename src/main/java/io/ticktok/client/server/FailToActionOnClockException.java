package io.ticktok.client.server;

import io.ticktok.client.TicktokException;

//TODO: Name is weird maybe FailToActOnClockException
public class FailToActionOnClockException extends TicktokException {

    public FailToActionOnClockException(String message) {
        super(message);
    }
}
