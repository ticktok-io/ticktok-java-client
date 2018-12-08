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
    private String queue;
    private String uri;
}
