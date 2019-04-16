package test.io.ticktok.client.server;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ClockRequest {

    private String name;
    private String schedule;

}
