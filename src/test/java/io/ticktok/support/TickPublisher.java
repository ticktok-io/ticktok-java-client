package io.ticktok.support;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TickPublisher {

    public static final String QUEUE_HOST = "localhost";
    public static final String QUEUE_EXCHANGE = "exchange";

    public static void publish() throws Exception {
        Connection connection = createConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(QUEUE_EXCHANGE, "fanout");

        String message = "tick";
        channel.basicPublish(QUEUE_EXCHANGE, "", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }

    private static Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(QUEUE_HOST);
        return factory.newConnection();
    }
}
