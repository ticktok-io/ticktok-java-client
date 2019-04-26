package test.io.ticktok.client.tick;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.ticktok.client.tick.ChannelException;
import io.ticktok.client.tick.TickChannel;
import io.ticktok.client.tick.TickListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class RabbitTickerTest {

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
                tickListener.forChannel(tickChannelFor(qn)).register(tickCount::incrementAndGet);
            });

            tickListener.disconnect();
            for (String qn : queueNames) {
                channel.basicPublish("", qn, null, "".getBytes());
            }

            sleep(1000);
            assertThat(tickCount.get(), is(0));
        }, "q1", "q2");
    }

    private TickChannel tickChannelFor(String qn) {
        return TickChannel.builder().type("rabbit").uri("amqp://localhost").queue(qn).build();
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

    @Test
    void replaceCallbackForAGivenChannel() throws Exception {
       withQueues((channel, queues) -> {
           CountDownLatch tickCountOld = new CountDownLatch(1);
           CountDownLatch tickCountNew = new CountDownLatch(1);
           tickListener.forChannel(tickChannelFor("q1")).register(tickCountOld::countDown);
           tickListener.forChannel(tickChannelFor("q1")).register(tickCountNew::countDown);

           channel.basicPublish("", "q1", null, "".getBytes());

           assertTimeoutPreemptively(ofSeconds(1), (Executable) tickCountNew::await);
           assertThat(tickCountOld.getCount(), is(0));
       }, "q1");
    }

    @AfterEach
    void disconnect() {
        tickListener.disconnect();
    }

    interface WithQueuesCallable {
        void call(Channel channel, List<String> queueNames) throws Exception;
    }
}