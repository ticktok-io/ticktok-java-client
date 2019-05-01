package io.ticktok.client.tick;

import io.ticktok.client.tick.http.HttpTickerPolicy;
import io.ticktok.client.tick.rabbit.RabbitTickerPolicy;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class TickListener {

    public static final String RABBIT = "rabbit";
    public static final String HTTP = "http";

    private final Map<String, Ticker> tickers = new HashMap<>();

    public TickListener() {
        tickers.put(RABBIT, new Ticker(new RabbitTickerPolicy()));
        tickers.put(HTTP, new Ticker(new HttpTickerPolicy()));
    }

    public TickRegistrar forChannel(TickChannel channel) {
        validateChannelType(channel);
        return new TickRegistrar(tickers.get(channel.getType()), channel);
    }

    private void validateChannelType(TickChannel channel) {
        if(!tickers.containsKey(channel.getType())) {
            throw new ChannelTypeUnsupportedException(
                    format("Channel type: %s is unsupported", channel.getType()));
        }
    }

    public void disconnect() {
        tickers.values().forEach(Ticker::disconnect);
    }

    public static class TickRegistrar {

        private final Ticker ticker;
        private final TickChannel channel;

        public TickRegistrar(Ticker ticker, TickChannel channel) {
            this.ticker = ticker;
            this.channel = channel;
        }

        public void register(TickConsumer consumer) {
            ticker.register(channel, consumer);
        }
    }

}
