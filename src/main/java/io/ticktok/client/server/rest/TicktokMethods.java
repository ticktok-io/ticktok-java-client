package io.ticktok.client.server.rest;

public enum  TicktokMethods {

    CREATE(201),
    GET(200),
    ACTION(204);

    public final int status;

    TicktokMethods(int status) {
        this.status = status;
    }
}
