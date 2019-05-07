package test.io.ticktok.client.server.support;

import com.google.gson.Gson;
import io.ticktok.client.server.ClockRequest;
import org.json.JSONObject;
import org.rockm.blink.BlinkRequest;
import org.rockm.blink.BlinkServer;
import java.io.IOException;

public class TicktokServer {

    private Boolean badResponse = false;
    private String latestTick;

    private BlinkServer server = new BlinkServer(1212) {{
        try {
            post("/api/v1/clocks", (req, res) -> {
                if (badResponse) {
                    res.status(400);
                    resetResponse();
                }
                else
                    res.status(201);
                ClockRequest request = parseRequest(req);
                return clockByNameResponse(request.getName(), request.getSchedule());
            });

            put("/api/v1/clocks/{id}/tick", (req, res) -> {
                res.status(204);
                latestTick = req.pathParam("id");
                return "";
            });

            get("/api/v1/clocks", (req, res) -> {
                res.status(200);
                return clockByNameResponse(req.param("name"), req.param("schedule"));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private ClockRequest parseRequest(BlinkRequest req) {
            return new Gson().fromJson(req.body(), ClockRequest.class);
        }

        private void resetResponse() {
            badResponse = false;
        }
    };

    public void returnBadRequest(){
        this.badResponse = true;
    }

    public String tickFor() {
        return latestTick;
    }

    private String clockByNameResponse(String name, String schedule) {
        final String id = "5cd179ad7f1c3fb809f5f0e8";
        return new JSONObject().
                put("id", id).
                put("name", name).
                put("schedule", schedule).
                put("status", "ACTIVE").
                put("url", "https://my.ticktok/api/v1/clocks/" + id).toString();
    }

    public void kill(){
        if (server != null)
            server.stop();
    }
}
