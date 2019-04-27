package io.ticktok.client.tick;

import io.ticktok.client.TicktokException;

public class ChannelTypeUnsupportedException extends TicktokException {

    public ChannelTypeUnsupportedException(String message) {
        super(message);
    }
}
