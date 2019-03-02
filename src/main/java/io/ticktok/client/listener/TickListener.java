package io.ticktok.client.listener;

import com.rabbitmq.client.*;
import io.ticktok.client.TicktokException;
import io.ticktok.client.register.RabbitChannel;
import io.ticktok.client.rest.ClockRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import static java.text.MessageFormat.format;

public class TickListener {

    public static void listen(RabbitChannel channel, Runnable runnable) throws TicktokException {
        com.rabbitmq.client.Channel tickChannel;
        try {
            tickChannel = listen(channel);
            Consumer consumer = consume(runnable, tickChannel);
            tickChannel.basicConsume(channel.getQueue(), true, consumer);
        } catch (IOException | TimeoutException e) {
            throw new TicktokException(format("Ticktok failed to connect to queue: {0}, with uri: {1}. follow trace: {2}",
                    channel.getQueue(), channel.getUri(), ExceptionUtils.getStackTrace(e)));
        }
    }

    private static Consumer consume(Runnable runnable, com.rabbitmq.client.Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) {
                runnable.run();
            }
        };
    }

    private static com.rabbitmq.client.Channel listen(RabbitChannel channel) throws IOException, TimeoutException {
        Connection connection = createConnection(channel);
        return connection.createChannel();
    }

    private static Connection createConnection(RabbitChannel channel) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(URI.create(channel.getUri()));
        } catch (Exception e) {
            throw new ClockRequest.TicktokInvalidValueException(format("Queue uri - {0} is invalid, failed to listen to queue", channel.getUri()));
        }
        return factory.newConnection();
    }
}
