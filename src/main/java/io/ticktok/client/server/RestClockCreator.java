package test.io.ticktok.client.server;

import com.google.gson.Gson;
import io.ticktok.client.TicktokOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Slf4j
public class RestClockCreator {
    public static final String REGISTER_NEW_CLOCK = "api/v1/clocks";

    private final String token;
    private final String domain;

    public RestClockCreator(TicktokOptions options) {
        this.domain = options.getDomain();
        this.token = options.getToken();
    }

    public Clock create(ClockRequest clockRequest) {
        try {
            HttpResponse httpResponse = Request.Post(createClockUrl())
                    .bodyString(new Gson().toJson(clockRequest), ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            validateResponse(httpResponse);
            return clockFrom(httpResponse.getEntity());
        } catch (IOException e) {
            throw new ConnectionException("Connection error", e);
        }
    }

    private String createClockUrl() {
        return this.domain + "/" + REGISTER_NEW_CLOCK + "?access_token=" + this.token;
    }

    private void validateResponse(HttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusLine().getStatusCode() != 201) {
            throw new FailToCreateClockException(EntityUtils.toString(httpResponse.getEntity()));
        }
    }

    private Clock clockFrom(HttpEntity entity) throws IOException {
        return new Gson().fromJson(EntityUtils.toString(entity), Clock.class);
    }
}
