package io.ticktok.client.rest;

import io.javalin.Javalin;
import io.ticktok.client.TicktokException;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.TicktokServerException;
import io.ticktok.client.register.Clock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RestTicktokClientTest {

    @Test
    void failOnServiceNotAvailable(){
        assertThrows(TicktokException.class, () -> buildRegisterRequest("every.5.seconds"));
    }

    private Clock buildRegisterRequest(String schedule) {
        return new RestTicktokClient(new TicktokOptions("http://localhost:1212", "my_token")).register(ClockRequest.create("clock_name", schedule));
    }

    @Test
    void failMsgShouldBeRespectable(){
        assertThrows(TicktokException.class, () -> buildRegisterRequest("every.5.seconds"), "fail to register clock");
    }

    @Test
    void catchBadRequestGivenInValidSchedule(){
        Javalin app = badRequestStub();
        assertThrows(TicktokServerException.class, () -> buildRegisterRequest("-"));
        app.stop();
    }

    private Javalin badRequestStub() {
        return Javalin.create().enableCaseSensitiveUrls().start(1212).post("/api/v1/clocks", ctx -> ctx.status(400));
    }
}