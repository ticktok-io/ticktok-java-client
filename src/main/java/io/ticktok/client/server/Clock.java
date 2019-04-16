package test.io.ticktok.client.server;

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

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    @EqualsAndHashCode
    @ToString
    public static class TickChannel {
        private String queue;
        private String uri;
    }

}
