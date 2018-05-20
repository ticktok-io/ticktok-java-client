package io.ticktok.register;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class ClockChannel {
    private String exchange;
    private String topic;
    private String uri;
}
