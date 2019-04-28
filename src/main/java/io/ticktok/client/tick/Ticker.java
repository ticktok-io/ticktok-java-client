package io.ticktok.client.tick;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Ticker {

    private final TickerPolicy policy;
    private Map<String, TickConsumerInvoker> consumers = new ConcurrentHashMap<>();

    public Ticker(TickerPolicy policy) {
        this.policy = policy;
    }

    public synchronized void register(TickChannel channel, TickConsumer consumer) {
        final String key = channel.getDetails().get(policy.idKey());
        if (!consumers.containsKey(key)) {
            TickConsumerInvoker invoker = policy.createConsumer(channel);
            consumers.put(key, invoker);
        }
        consumers.get(key).setTickConsumer(consumer);
    }

    public void disconnect() {
        policy.disconnect();
        consumers.clear();
    }
}
