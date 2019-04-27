package io.ticktok.client;

public class TicktokException extends RuntimeException {

    public TicktokException(String message) {
        super(ticktokMessage(message));
    }

    private static String ticktokMessage(String message) {
        return "(Ticktok) " + message;
    }

    public TicktokException(String message, Throwable cause) {
        super(ticktokMessage(message), cause);
    }
}
