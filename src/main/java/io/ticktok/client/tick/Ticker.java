package io.ticktok.client.tick;

public interface Ticker {

    void register(TickChannel channel, TickConsumer consumer);

    void disconnect();
}
