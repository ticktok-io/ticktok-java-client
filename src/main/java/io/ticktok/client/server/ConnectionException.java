package io.ticktok.client.server;

import io.ticktok.client.TicktokException;

public class ConnectionException extends TicktokException {
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
