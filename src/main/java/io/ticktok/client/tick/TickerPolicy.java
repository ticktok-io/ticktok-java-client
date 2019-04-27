package io.ticktok.client.tick;

public interface TickerPolicy {
    TickConsumerInvoker createConsumer(TickChannel channel, TickConsumer consumer);

    String idKey();

    void disconnect();
}
