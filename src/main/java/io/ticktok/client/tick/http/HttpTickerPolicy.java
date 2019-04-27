package io.ticktok.client.tick.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.ticktok.client.tick.TickChannel;
import io.ticktok.client.tick.TickConsumer;
import io.ticktok.client.tick.TickConsumerInvoker;
import io.ticktok.client.tick.TickerPolicy;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class HttpTickerPolicy implements TickerPolicy {

    public static final String URL_PARAM = "url";
    private final HttpClient httpClient = HttpClients.custom()
            .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
            .build();
    private Timer timer;

    @Override
    public TickConsumerInvoker createConsumer(TickChannel channel, TickConsumer consumer) {
        createTimerIfNeeded();
        String url = channel.getDetails().get(URL_PARAM);
        final HttpTickConsumerInvoker tickConsumerInvoker = new HttpTickConsumerInvoker(url, consumer);
        timer.scheduleAtFixedRate(tickConsumerInvoker, 0L, 1000L);
        return tickConsumerInvoker;
    }

    private void createTimerIfNeeded() {
        if(timer == null) {
           timer = new Timer();
        }
    }

    @Override
    public String idKey() {
        return URL_PARAM;
    }

    @Override
    public void disconnect() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Setter
    private class HttpTickConsumerInvoker extends TimerTask implements TickConsumerInvoker {

        private TickConsumer tickConsumer;
        private final String url;

        public HttpTickConsumerInvoker(String url, TickConsumer tickConsumer) {
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
