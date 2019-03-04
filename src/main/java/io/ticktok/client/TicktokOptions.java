package io.ticktok.client;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class TicktokOptions {
    private String domain;
    private String token;
}
