package test.io.ticktok.client.tick;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.ticktok.client.server.Clock;
import io.ticktok.client.tick.ChannelException;
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

class TickListenerTest {

    private final TickListener tickListener = new TickListener();

    @Test
    void failWhenRabbitConnectionFailed() {
        assertThrows(ChannelException.class, () -> {
            listenOn("amqp://unknown");
        });
    }

    private void listenOn(String uri) {
        tickListener.listen(Clock.TickChannel.builder().uri(uri).build(), () -> {
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
                tickListener.listen(Clock.TickChannel.builder().uri("amqp://localhost").queue(qn).build(), tickCount::incrementAndGet);
            });

            tickListener.disconnect();
            for (String qn : queueNames) {
                channel.basicPublish("", qn, null, "".getBytes());
            }

            sleep(2000);
            assertThat(tickCount.get(), is(0));
        }, "q1", "q2");
    }

    private void withQueues(QithQueuesCallable callable, String... queueNames) throws Exception {
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

    interface QithQueuesCallable {
        void call(Channel channel, List<String> queueNames) throws Exception;
    }
}