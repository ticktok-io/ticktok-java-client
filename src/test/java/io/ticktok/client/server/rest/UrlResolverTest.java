package io.ticktok.client.server.rest;

import io.ticktok.client.TicktokOptions;
import org.junit.jupiter.api.Test;

import static org.testng.Assert.assertEquals;

public class UrlResolverTest {

    private final TicktokOptions options = new TicktokOptions().domain("http://localhost:9999/my.ticktok/").token("my_token");

    @Test
    void resolveDefault(){
        String url = new UrlResolver(options).resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks?access_token=my_token", url);
    }

    @Test
    void resolveWithPathParam(){
        String url = new UrlResolver(options).pathParam("123").resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks/123?access_token=my_token", url);
    }

    @Test
    void resolveWithMultiPathParam(){
        String url = new UrlResolver(options).
                pathParam("123").
                pathParam("name").
                pathParam("clock").
                resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks/123/name/clock?access_token=my_token", url);
    }

    @Test
    void resolveWithQueryParam(){
        String url = new UrlResolver(options).
                queryParam("name", "my_clock").resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks?access_token=my_token&name=my_clock", url);
    }

    @Test
    void resolveMultiQueryParam(){
        String url = new UrlResolver(options).
                queryParam("name", "my_clock").
                queryParam("schedule", "@never").
                queryParam("id", "my_id").
                resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks?access_token=my_token&name=my_clock&schedule=@never&id=my_id", url);
    }

    @Test
    void resolveWithMixedParamTypes(){
        String url = new UrlResolver(options).
                queryParam("name", "my_clock").
                pathParam("1234").
                queryParam("id", "my_id").
                resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks/1234?access_token=my_token&name=my_clock&id=my_id", url);
    }

    @Test
    void noStateSaved(){
        UrlResolver urlResolver = new UrlResolver(options);
        String url = urlResolver.pathParam("1234").resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks/1234?access_token=my_token", url);
        url = urlResolver.queryParam("id", "1234").resolve();
        assertEquals("http://localhost:9999/my.ticktok/api/v1/clocks?access_token=my_token&id=1234", url);
    }
}