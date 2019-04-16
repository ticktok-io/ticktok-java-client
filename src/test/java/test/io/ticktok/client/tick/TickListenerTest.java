package test.io.ticktok.client.tick;

import io.ticktok.client.tick.ChannelException;
import io.ticktok.client.tick.TickListener;
import test.io.ticktok.client.server.Clock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TickListenerTest {

    @Test
    void failWhenRabbitConnectionFailed() {
        assertThrows(ChannelException.class, () -> {
            listenOn("amqp://unknown");
        });
    }

    private void listenOn(String uri) {
        new TickListener().listen(Clock.TickChannel.builder().uri(uri).build(), () -> {
        });
    }

    @Test
    void failOnInvalidUri() {
        assertThrows(ChannelException.class, () -> {
            listenOn("invalid uri");
        });
    }
}