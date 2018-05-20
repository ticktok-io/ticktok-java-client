package io.ticktok.support;

import com.google.gson.Gson;
import io.ticktok.register.Clock;
import org.rockm.blink.BlinkRequest;
import org.rockm.blink.BlinkServer;

import java.io.IOException;

public class TicktockServiceStub {

    public TicktockServiceStub(int port) throws IOException {

        new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) ->
                    returnClock(req)
            );
        }

            private String returnClock(BlinkRequest req) {
                return new Gson().toJson(new Clock().builder().
                        id("123").
                        schedule(extractBody(req)).
                        url("yourQueueUrl").
                        build());
            }

            private String extractBody(BlinkRequest req) {
                return new Gson().fromJson(req.body(), ClockRequest.class).schedule;
            }
        };
    }

    private class ClockRequest{
        private String schedule = "";

        public ClockRequest(String schedule){
            this.schedule = schedule;
        }

    }
}
