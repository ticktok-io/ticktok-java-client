package io.ticktok.client.register;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class RabbitChannel {
    private String queue;
    private String uri;
}
