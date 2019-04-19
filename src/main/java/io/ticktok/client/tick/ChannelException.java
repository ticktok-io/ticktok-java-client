package io.ticktok.client.tick;

import io.ticktok.client.TicktokException;

public class ChannelException extends TicktokException {
    public ChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
