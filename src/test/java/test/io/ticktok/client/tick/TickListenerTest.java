package test.io.ticktok.client.tick;

import io.ticktok.client.tick.TickChannel;
import io.ticktok.client.tick.TickListener;
import io.ticktok.client.tick.ChannelTypeUnsupportedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TickListenerTest {

    @Test
    void failOnUnsupportedChannelType() {
        assertThrows(ChannelTypeUnsupportedException.class, () -> {
            new TickListener().forChannel(TickChannel.builder().type("unknown").build()).register(() -> {});
        });
    }
}