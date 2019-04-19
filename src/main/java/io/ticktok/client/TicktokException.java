package io.ticktok.client;

public class TicktokException extends RuntimeException {

    public TicktokException(String message) {
        super(message);
    }

    public TicktokException(String message, Throwable cause) {
        super("(Ticktok) " + message, cause);
    }
}
