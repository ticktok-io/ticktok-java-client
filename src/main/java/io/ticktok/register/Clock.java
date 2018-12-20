package io.ticktok.register;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Clock {
    private Channel channel;
    private String id;
    private String schedule;
    private String url;
    private String name;
}
