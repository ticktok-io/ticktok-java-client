package io.ticktok.client.listener;

import com.rabbitmq.client.*;
import io.ticktok.client.TickConsumer;
import io.ticktok.client.TicktokException;
import io.ticktok.client.register.TickChannel;
import io.ticktok.client.rest.ClockRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import static java.text.MessageFormat.format;

@Slf4j
public class TickListener {

    public void listen(TickChannel channel, TickConsumer tickConsumer) {
        com.rabbitmq.client.Channel tickChannel;
        try {
            tickChannel = listen(channel);
            Consumer consumer = consume(tickConsumer, tickChannel);
            tickChannel.basicConsume(channel.getQueue(), true, consumer);
            log.debug("now listening on queue : {}", channel.getQueue());
        } catch (IOException | TimeoutException e) {
            throw new TicktokException(format("Ticktok failed to connect to queue: {0}, with uri: {1}. follow trace: {2}",
                    channel.getQueue(), channel.getUri(), ExceptionUtils.getStackTrace(e)));
        }
    }

    private Consumer consume(TickConsumer runnable, com.rabbitmq.client.Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                runnable.consume();
            }
        };
    }

    private com.rabbitmq.client.Channel listen(TickChannel channel) throws IOException, TimeoutException {
        Connection connection = createConnection(channel);
        return connection.createChannel();
    }

    private Connection createConnection(TickChannel channel) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(URI.create(channel.getUri()));
        } catch (Exception e) {
            throw new ClockRequest.TicktokInvalidValueException(format("Queue uri - {0} is invalid, failed to listen to queue", channel.getUri()));
        }
        return factory.newConnection();
    }

}
