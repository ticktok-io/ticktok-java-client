package io.ticktok.client.tick;

import com.rabbitmq.client.*;
import io.ticktok.client.server.Clock;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static java.text.MessageFormat.format;

public class TickListener {

    private Connection connection;
    private Channel channel;
    private final Object lock = new Object();

    public void listen(Clock.TickChannel tickChannel, TickConsumer tickConsumer) {
        try {
            createChannelFor(tickChannel.getUri());
            channel.basicConsume(tickChannel.getQueue(), true, consumerFor(channel, tickConsumer));
        } catch (Exception e) {
            throw new ChannelException(format("Failed to connect to queue: {0}, with uri: {1}",
                    tickChannel.getQueue(), tickChannel.getUri()), e);
        }
    }

    private void createChannelFor(String uri) throws Exception {
        synchronized (lock) {
            if(connection == null) {
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

    private Consumer consumerFor(Channel channel, final TickConsumer consumer) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                consumer.consume();
            }
        };
    }

    public void disconnect() {
        closeChannel();
        closeConnection();
    }

    private void closeChannel() {
        try {
            if(channel != null) {
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
            if(connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            //ignore
        } finally {
            connection = null;
        }

    }
}
