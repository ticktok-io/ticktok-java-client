package io.ticktok.client.tick;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HttpTicker implements Ticker {

    private final HttpClient httpClient = HttpClients.custom()
            .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
            .build();
    private final Timer timer = new Timer();
    private Map<String, TickConsumerWrapper> consumers = new ConcurrentHashMap<>();

    @Override
    public void register(TickChannel channel, TickConsumer consumer) {
        final String url = channel.getDetails().get("url");
        if (!consumers.containsKey(url)) {
            scheduleNewConsumer(consumer, url);
        }
        updateConsumer(consumer, url);
    }

    private void updateConsumer(TickConsumer consumer, String url) {
        final TickConsumerWrapper tickConsumerWrapper = consumers.get(url);
        tickConsumerWrapper.setTickConsumer(consumer);
    }

    private void scheduleNewConsumer(TickConsumer consumer, String url) {
        final TickConsumerWrapper tickConsumerWrapper = new TickConsumerWrapper(url, consumer);
        consumers.put(url, tickConsumerWrapper);
        timer.scheduleAtFixedRate(tickConsumerWrapper, 0L, 1000L);
    }

    @Override
    public void disconnect() {
        consumers.values().forEach(TimerTask::cancel);
        timer.purge();
    }

    @Setter
    private class TickConsumerWrapper extends TimerTask {

        private TickConsumer tickConsumer;
        private final String url;

        public TickConsumerWrapper(String url, TickConsumer tickConsumer) {
            this.tickConsumer = tickConsumer;
            this.url = url;
        }

        @Override
        public void run() {
            if (ticksAvailableOn(url)) {
                tickConsumer.consume();
            }
        }

        private boolean ticksAvailableOn(String url) {
            try {
                HttpResponse response = httpClient.execute(new HttpGet(url));
                return isOk(response) && ticksFrom(response).size() > 0;
            } catch (IOException e) {
                log.warn("Failed to fetch ticks", e);
            }
            return false;
        }

        private boolean isOk(HttpResponse response) {
            return response.getStatusLine().getStatusCode() == 200;
        }

        private JsonArray ticksFrom(HttpResponse response) throws IOException {
            return new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonArray.class);
        }
    }
}
