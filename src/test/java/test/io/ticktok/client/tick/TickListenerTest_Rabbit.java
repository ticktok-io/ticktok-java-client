package test.io.ticktok.client.tick;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.ticktok.client.tick.ChannelException;
import io.ticktok.client.tick.TickChannel;
import io.ticktok.client.tick.TickListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TickListenerTest_Rabbit {

    private final TickListener tickListener = new TickListener();

    @Test
    void failWhenRabbitConnectionFailed() {
        assertThrows(ChannelException.class, () -> {
            listenOn("amqp://unknown");
        });
    }

    private void listenOn(String uri) {
        tickListener.forChannel(TickChannel.builder().uri(uri).build()).register(() -> {
        });
    }

    @Test
    void failOnInvalidUri() {
        assertThrows(ChannelException.class, () -> {
            listenOn("invalid uri");
        });
    }

    @Test
    void ignoreErrorWhenDisconnecting() {
        tickListener.disconnect();
    }

    @Test
    void disconnectAllClocks() throws Exception {
        withQueues((channel, queueNames) -> {
        AtomicInteger tickCount = new AtomicInteger();
            queueNames.forEach(qn -> {
                tickListener.forChannel(TickChannel.builder().uri("amqp://localhost").queue(qn).build()).register(tickCount::incrementAndGet);
            });

            tickListener.disconnect();
            for (String qn : queueNames) {
                channel.basicPublish("", qn, null, "".getBytes());
            }

            sleep(2000);
            assertThat(tickCount.get(), is(0));
        }, "q1", "q2");
    }

    private void withQueues(WithQueuesCallable callable, String... queueNames) throws Exception {
        Connection connection = new ConnectionFactory().newConnection();
        Channel channel = connection.createChannel();
        for (String qn : queueNames) {
            channel.queueDeclare(qn, false, false, true, null);
        }

        callable.call(channel, Arrays.asList(queueNames));
        channel.close();
        connection.close();
    }

    @AfterEach
    void disconnect() {
        tickListener.disconnect();
    }

    interface WithQueuesCallable {
        void call(Channel channel, List<String> queueNames) throws Exception;
    }
}