package io.ticktok.Listener;

import com.rabbitmq.client.*;
import io.ticktok.Ticktok.TicktokException;
import io.ticktok.register.Channel;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;

@Slf4j
public class TickListener {

    public static void listen(Channel channel, Runnable runnable) throws TicktokException {
        try {
            com.rabbitmq.client.Channel tickChannel = listen(channel);
            Consumer consumer = consume(runnable, tickChannel);
            tickChannel.basicConsume(channel.getQueue(), true, consumer);
            log.debug("now listening on queue : {}", channel.getQueue());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TicktokException(e.getMessage());
        }
    }

    private static Consumer consume(Runnable runnable, com.rabbitmq.client.Channel channel) {
        return new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) {
                        log.debug("received tick on: {}, invoking..", channel.getConnection().getClientProvidedName());
                        runnable.run();
                    }
                };
    }

    private static com.rabbitmq.client.Channel listen(Channel channel) throws Exception {
        Connection connection = createConnection(channel);
        return connection.createChannel();
    }

    private static Connection createConnection(Channel channel) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(new URI(channel.getUri()));
        return factory.newConnection();
    }
}
