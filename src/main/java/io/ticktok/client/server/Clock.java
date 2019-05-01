package io.ticktok.client.server;

import io.ticktok.client.tick.TickChannel;
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

}
