package io.ticktok.client.tick;

import com.rabbitmq.client.*;
import io.ticktok.client.server.Clock;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import static java.text.MessageFormat.format;

@Slf4j
public class TickListener {

    private Connection connection;
    private Channel channel;

    public void listen(Clock.TickChannel tickChannel, TickConsumer tickConsumer) {
        try {
            channel = createChannelFor(tickChannel.getUri());
            channel.basicConsume(tickChannel.getQueue(), true, consumerFor(channel, tickConsumer));
        } catch (Exception e) {
            throw new ChannelException(format("Failed to connect to queue: {0}, with uri: {1}",
                    tickChannel.getQueue(), tickChannel.getUri()), e);
        }
    }

    private Channel createChannelFor(String uri) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URI.create(uri));
        connection = factory.newConnection();
        return connection.createChannel();
    }

    private Consumer consumerFor(Channel channel, TickConsumer runnable) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                runnable.consume();
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
        } catch (IOException | TimeoutException e) {
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
