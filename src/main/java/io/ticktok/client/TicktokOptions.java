package io.ticktok.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class TicktokOptions {
    private String domain;
    private String token;

    public TicktokOptions domain(String domain) {
        this.domain = domain;
        return this;
    }

    public TicktokOptions token(String token) {
        this.token = token;
        return this;
    }
}
