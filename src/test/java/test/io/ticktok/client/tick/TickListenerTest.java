package test.io.ticktok.client.tick;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.ticktok.client.server.Clock;
import io.ticktok.client.tick.ChannelException;
import io.ticktok.client.tick.TickListener;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

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

    void disconnect() throws Exception {
        Connection connection = new ConnectionFactory().newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("q1", false, false, false, null);
        channel.queueDeclare("q2", false, false, false, null);
        CountDownLatch tickCount = new CountDownLatch(2);
        tickListener.listen(Clock.TickChannel.builder().uri("amqp://localhost").queue("q1").build(), tickCount::countDown);
        tickListener.listen(Clock.TickChannel.builder().uri("amqp://localhost").queue("q2").build(), tickCount::countDown);

        tickListener.disconnect();
        channel.basicPublish("", "q1", null, "".getBytes());
        channel.basicPublish("", "q2", null, "".getBytes());

        tickCount.await(2, TimeUnit.SECONDS);
        Assert.assertThat(tickCount.getCount(), is(2L));

        channel.close();
        connection.close();
    }

    @AfterEach
    void disconnect() {
        tickListener.disconnect();
    }
}