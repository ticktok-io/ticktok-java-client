package io.ticktok.client.register;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Clock {
    private RabbitChannel channel;
    private String id;
    private String schedule;
    private String url;
    private String name;
}
