package io.ticktok.support;

import org.rockm.blink.BlinkServer;

import java.io.IOException;

public class TicktockServiceStub {

    private ClockRequest clockRequest;

    public TicktockServiceStub(int port) throws IOException {

        new BlinkServer(port) {{
            post("/api/v1/clocks", (req, res) ->
                    clockRequest = new ClockRequest(req.body(), req.header("Authorization"))
            );
        }};
    }


    public String getBody(){
        return this.clockRequest.body;
    }

    public String getToken(){
        return this.clockRequest.token;
    }

    private class ClockRequest{
        private String body = "";
        private String token = "";

        public ClockRequest(String body, String token){
            this.body = body;
            this.token = token;
        }

        public String getBody(){
            return this.body;
        }

        public String getToken(){
            return this.token;
        }
    }
}
