package io.ticktok.handler;

import com.rabbitmq.client.*;
import io.ticktok.register.Clock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class TickTokHandler implements TickHandler {

    @Override
    public void handleTick(Clock clock) throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {

        /*
        * "exchange": "string",
            "topic": "string",
            "uri": "string"
        * */

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(clock.getClockChannel().getUri() /*"amqp://alpha.netkiller.cn"*/);
        factory.setHost("localhost");
        Connection connection = null;
        connection = factory.newConnection();

        Channel channel = connection.createChannel();
        channel.exchangeDeclare(clock.getClockChannel().getExchange(), BuiltinExchangeType.DIRECT);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
//        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
