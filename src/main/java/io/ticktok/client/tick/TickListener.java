package io.ticktok.client.tick;

import com.rabbitmq.client.*;
import test.io.ticktok.client.server.Clock;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import static java.text.MessageFormat.format;

@Slf4j
public class TickListener {

    public void listen(Clock.TickChannel tickChannel, TickConsumer tickConsumer) {
        Channel channel = null;
        try {
            channel = createChannelFor(tickChannel.getUri());
            channel.basicConsume(tickChannel.getQueue(), true, consumerFor(channel, tickConsumer));
        } catch (Exception e) {
            throw new ChannelException(format("Ticktok failed to connect to queue: {0}, with uri: {1}",
                    tickChannel.getQueue(), tickChannel.getUri()), e);
        }
    }

    private Channel createChannelFor(String uri) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URI.create(uri));
        final Connection connection = factory.newConnection();
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
}
