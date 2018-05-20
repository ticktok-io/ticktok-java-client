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
    private ClockChannel clockChannel;
    private String id;
    private String schedule;
    private String url;
}
