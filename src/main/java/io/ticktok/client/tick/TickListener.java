package io.ticktok.client.tick;

import java.util.HashMap;
import java.util.Map;

public class TickListener {

    private final Map<String, Ticker> tickers = new HashMap<>();

    public TickListener() {
        tickers.put("rabbit", new RabbitTicker());
        tickers.put("http", new HttpTicker());
    }

    public TickRegistrator forChannel(TickChannel channel) {
        return new TickRegistrator(tickers.get(channel.getType()), channel);
    }

    public void disconnect() {
        tickers.values().forEach(Ticker::disconnect);
    }

    public static class TickRegistrator {

        private final Ticker ticker;
        private final TickChannel channel;

        public TickRegistrator(Ticker ticker, TickChannel channel) {
            this.ticker = ticker;
            this.channel = channel;
        }

        public void register(TickConsumer consumer) {
            ticker.register(channel, consumer);
        }
    }
}
