package io.ticktok.client.tick;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class TickChannel {
    private String type;
    private Map<String, String> details;

    private String queue;
    private String uri;
}
