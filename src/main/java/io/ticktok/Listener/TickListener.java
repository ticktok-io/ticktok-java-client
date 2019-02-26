package io.ticktok.Listener;

import com.rabbitmq.client.*;
import io.ticktok.Ticktok.TicktokException;
import io.ticktok.register.Channel;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.text.MessageFormat;

import static io.ticktok.rest.ClockRequest.TicktokInvalidValueException;

public class TickListener {

    public static void listen(Channel channel, Runnable runnable) throws TicktokException {
        try {
            com.rabbitmq.client.Channel tickChannel = listen(channel);
            Consumer consumer = consume(runnable, tickChannel);
            tickChannel.basicConsume(channel.getQueue(), true, consumer);
        } catch (Exception e) {
            throw new TicktokException(MessageFormat.format("Ticktok failed to connect to queue: {0}, with uri: {1}. follow trace: {2}",
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

    private static com.rabbitmq.client.Channel listen(Channel channel) throws Exception {
        Connection connection = createConnection(channel);
        return connection.createChannel();
    }

    private static Connection createConnection(Channel channel) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(channel.getUri());
            return factory.newConnection();
        } catch (Exception e) {
            throw new TicktokInvalidValueException(MessageFormat.format("Queue uri - {0} is invalid, failed to listen to queue", channel.getUri()));
        }
    }
}
