package io.ticktok.rest;

import io.javalin.Javalin;
import io.ticktok.Ticktok;
import io.ticktok.TicktokOptions;
import io.ticktok.register.Clock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestTicktokClientTest {

    @Test
    public void failOnServiceNotAvailable(){
        assertThrows(Ticktok.TicktokException.class, () -> buildRegisterRequest("every.5.seconds"));
    }

    private Clock buildRegisterRequest(String schedule) {
        return new RestTicktokClient(new TicktokOptions("http://localhost:1212", "my_token")).register(ClockRequest.create("clock_name", schedule));
    }

    @Test
    public void failMsgShouldBeRespectable(){
        assertThrows(Ticktok.TicktokException.class, () -> buildRegisterRequest("every.5.seconds"), "fail to register clock");
    }

    @Test
    public void catchBadRequestGivenInValidSchedule(){
        Javalin app = badRequestStub();
        assertThrows(Ticktok.TicktokServerException.class, () -> buildRegisterRequest("-"));
        app.stop();
    }

    private Javalin badRequestStub() {
        return Javalin.create().enableCaseSensitiveUrls().start(1212).post("/api/v1/clocks", ctx -> ctx.result("bad clock schedule phrase").status(400));
    }
}