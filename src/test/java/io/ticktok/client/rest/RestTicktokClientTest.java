package io.ticktok.client.rest;

import io.ticktok.client.TicktokException;
import io.ticktok.client.TicktokOptions;
import io.ticktok.client.TicktokServerException;
import io.ticktok.client.register.Clock;
import org.junit.jupiter.api.Test;
import org.rockm.blink.BlinkServer;

import java.io.IOException;

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
    void catchBadRequestGivenInValidSchedule() throws IOException {
        BlinkServer stub = badRequestStub();
        assertThrows(TicktokServerException.class, () -> buildRegisterRequest("-"));
        stub.stop();
    }

    private BlinkServer badRequestStub() throws IOException {
        return new BlinkServer(1212) {{
            post("/api/v1/clocks", (req, res) -> {
                res.status(400);
                return "Bad Request";
            });
        }};
    }
}