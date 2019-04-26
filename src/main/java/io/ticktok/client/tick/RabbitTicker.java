package io.ticktok.client.tick;

import com.rabbitmq.client.*;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.text.MessageFormat.format;


public class RabbitTicker implements Ticker {

    private Connection connection;
    private Channel channel;
    private final Object lock = new Object();
    private Map<String, TickConsumerWrapper> consumers = new ConcurrentHashMap<>();

    @Override
    public void register(TickChannel tickChannel, TickConsumer consumer) {
        try {
            createChannelIfNeededOn(tickChannel.getUri());
            if (!consumers.containsKey(tickChannel.getQueue())) {
                final TickConsumerWrapper tickConsumerWrapper = new TickConsumerWrapper(channel);
                consumers.put(tickChannel.getQueue(), tickConsumerWrapper);
                channel.basicConsume(
                        tickChannel.getQueue(),
                        true,
                        tickConsumerWrapper);
            }
            consumers.get(tickChannel.getQueue()).setTickConsumer(consumer);
        } catch (Exception e) {
            throw new ChannelException(format("Failed to connect to queue: {0}, with uri: {1}",
                    tickChannel.getQueue(), tickChannel.getUri()), e);
        }
    }

    private void createChannelIfNeededOn(String uri) throws Exception {
        synchronized (lock) {
            if (connection == null) {
                connection = createConnectionFactoryFor(uri).newConnection();
                channel = connection.createChannel();
            }
        }
    }

    private ConnectionFactory createConnectionFactoryFor(String uri) throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URI.create(uri));
        return factory;
    }

    private Consumer consumerFor(Channel channel, String queue, final TickConsumer consumer) {
        consumers.putIfAbsent(queue, new TickConsumerWrapper(channel));
        final TickConsumerWrapper consumerWrapper = consumers.get(queue);
        consumerWrapper.setTickConsumer(consumer);
        return consumerWrapper;
    }

    public void disconnect() {
        closeChannel();
        closeConnection();
        consumers.clear();
    }

    private void closeChannel() {
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (Exception e) {
            //ignore
        } finally {
            channel = null;
        }
    }

    private void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            //ignore
        } finally {
            connection = null;
        }
    }

    @Setter
    private class TickConsumerWrapper extends DefaultConsumer {

        private TickConsumer tickConsumer;

        public TickConsumerWrapper(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            tickConsumer.consume();
        }
    }
}
