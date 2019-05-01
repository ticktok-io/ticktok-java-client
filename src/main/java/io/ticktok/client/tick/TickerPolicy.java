package io.ticktok.client.tick;

public interface TickerPolicy {
    TickConsumerInvoker createConsumer(TickChannel channel);

    String idKey();

    void disconnect();
}
