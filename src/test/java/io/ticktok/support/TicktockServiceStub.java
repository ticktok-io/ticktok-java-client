package io.ticktok.support;

import com.google.gson.Gson;
import io.ticktok.register.Clock;
import io.ticktok.register.ClockChannel;
import org.rockm.blink.BlinkRequest;
import org.rockm.blink.BlinkServer;

import java.io.IOException;

public class TicktockServiceStub {

    private BlinkServer ticktokService;
    public ClockRequest lastClockRequest;

    public TicktockServiceStub(int port) throws IOException {

        ticktokService = new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) ->{
                lastClockRequest = new Gson().fromJson(req.body(), ClockRequest.class);
                return returnClock(req) ;
                }
            );
        }

            private String returnClock(BlinkRequest req) {
                return new Gson().toJson(new Clock().builder().
                        id("123").
                        schedule(extractBody(req)).
                        url("localhost").
                        clockChannel(ClockChannel.builder().exchange("exchange").topic("myTopic").uri("localhost").build()).
                        build());
            }

            private String extractBody(BlinkRequest req) {
                return new Gson().fromJson(req.body(), ClockRequest.class).schedule;
            }
        };

    }

    /**
     * {
     *   "channel": {
     *     "exchange": "string",
     *     "topic": "string",
     *     "uri": "string"
     *   },
     *   "id": "string",
     *   "schedule": "string",
     *   "url": "string"
     * }
     */
    public class ClockRequest{
        public String schedule = "";

        public ClockRequest(String schedule){
            this.schedule = schedule;
        }
    }
}
