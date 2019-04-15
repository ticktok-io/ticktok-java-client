package io.ticktok.client.register;

import io.ticktok.client.TickConsumer;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Clock {
    private TickChannel channel;
    private String id;
    private String schedule;
    private String url;
    private String name;

    public void onTick(TickConsumer callback) {

    }
}
