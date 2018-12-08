package io.ticktok.Listener;

import com.rabbitmq.client.*;
import io.ticktok.Ticktok.TicktokException;
import io.ticktok.register.ClockChannel;

import java.io.IOException;
import java.net.URI;

public class TickListener {

    public static void listen(ClockChannel clockChannel, Runnable runnable) throws TicktokException {
        try {
            Channel channel = listen(clockChannel);
            Consumer consumer = consume(runnable, channel);
            channel.basicConsume(clockChannel.getQueue(), true, consumer);
        } catch (Exception e) {
            throw new TicktokException(e.getMessage());
        }
    }

    private static Consumer consume(Runnable runnable, Channel channel) {
        return new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body)
                            throws IOException {
                        String message = new String(body, "UTF-8");
                        System.out.println(" [x] Received '" + message + "'");
                        runnable.run();
                    }
                };
    }

    private static Channel listen(ClockChannel clockChannel) throws Exception {
        Connection connection = createConnection(clockChannel);
        Channel channel = connection.createChannel();
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        return channel;
    }

    private static Connection createConnection(ClockChannel clockChannel) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(new URI(clockChannel.getUri()));
        return factory.newConnection();
    }
}
