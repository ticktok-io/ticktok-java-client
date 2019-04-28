package io.ticktok.client.tick.rabbit;

import com.rabbitmq.client.*;
import io.ticktok.client.tick.*;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static java.text.MessageFormat.format;


public class RabbitTickerPolicy implements TickerPolicy {

    public static final String URI_PARAM = "uri";
    public static final String QUEUE_PARAM = "queue";

    private final Object lock = new Object();
    private Connection connection;
    private Channel channel;

    @Override
    public TickConsumerInvoker createConsumer(TickChannel tickChannel) {
        try {
            createChannelIfNeededOn(tickChannel.getDetails().get(URI_PARAM));
            return createScheduledInvokerFor(tickChannel);
        } catch (Exception e) {
            throw new ChannelException(format("Failed to connect to queue: {0}, with uri: {1}",
                    tickChannel.getDetails().get(QUEUE_PARAM), tickChannel.getDetails().get(URI_PARAM)), e);
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

    private RabbitTickConsumerInvoker createScheduledInvokerFor(TickChannel tickChannel) throws IOException {
        final RabbitTickConsumerInvoker rabbitTickConsumerInvoker = new RabbitTickConsumerInvoker(channel);
        channel.basicConsume(
                tickChannel.getDetails().get(QUEUE_PARAM),
                true,
                rabbitTickConsumerInvoker);
        return rabbitTickConsumerInvoker;
    }

    @Override
    public String idKey() {
        return "queue";
    }

    @Override
    public void disconnect() {
        closeChannel();
        closeConnection();
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
    private class RabbitTickConsumerInvoker extends DefaultConsumer implements TickConsumerInvoker {

        private TickConsumer tickConsumer;

        public RabbitTickConsumerInvoker(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            tickConsumer.consume();
        }
    }
}
