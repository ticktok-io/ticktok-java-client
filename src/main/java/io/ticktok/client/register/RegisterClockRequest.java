package io.ticktok.client.register;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
public class RegisterClockRequest {
    private String name;
    private String schedule;
}
