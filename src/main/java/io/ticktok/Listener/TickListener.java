package io.ticktok.Listener;

import com.rabbitmq.client.*;
import io.ticktok.Ticktok;
import io.ticktok.Ticktok.TicktokException;
import io.ticktok.register.ClockChannel;

import java.io.IOException;
import java.net.URI;

public class TickListener {

    private final static String QUEUE_NAME = "ticktok-queue";

    public static void listen(ClockChannel clockChannel, Runnable runnable) throws TicktokException{
        try {
            Connection connection = createConnection(clockChannel);
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    runnable.run();
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception e) {
            throw new TicktokException(e.getMessage());
        }
    }

    private static Connection createConnection(ClockChannel clockChannel) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(new URI(clockChannel.getUri()));
        return factory.newConnection();
    }
}
