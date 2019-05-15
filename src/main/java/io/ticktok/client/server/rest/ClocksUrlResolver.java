package io.ticktok.client.server.rest;

import io.ticktok.client.TicktokException;
import io.ticktok.client.TicktokOptions;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClocksUrlResolver {

    public static final String CLOCK_ROOT = "api/v1/clocks";
    private StringBuilder resolved = new StringBuilder();
    private List<NameValuePair> qParams = new ArrayList<>();
    private TicktokOptions options;

    public ClocksUrlResolver(TicktokOptions options){
        this.options = options;
        bootstrapResolver();
    }

    private void bootstrapResolver() {
        resolved.append(options.getDomain()).append(shouldAppendBackSlash(options.getDomain()) ? "/" : "").append(CLOCK_ROOT);
    }

    private boolean shouldAppendBackSlash(String domain) {
        return !domain.substring(domain.length() - 1).equals("/");
    }

    public String resolve() {
        String result = buildUri();
        result = decodeUri(result);
        resetQparams();
        resetResolver();
        return result;
    }

    private String decodeUri(String result) {
        try {
            return URLDecoder.decode(result, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new TicktokException("Fail to decode uri, root cause: " + e);
        }
    }

    private String buildUri() {
        try {
            return new URIBuilder(resolved.toString()).
                        setParameters(qParams).
                        setParameter("access_token", options.getToken()).build().toString();
        } catch (URISyntaxException e) {
            throw new TicktokException("URI is invalid, root cause: " + e);
        }
    }

    private void resetQparams() {
        qParams = new ArrayList<>();
    }

    private void resetResolver() {
        resolved = new StringBuilder();
        bootstrapResolver();
    }

    public ClocksUrlResolver pathParam(String s) {
        resolved.append("/").append(s);
        return this;
    }

    public ClocksUrlResolver queryParam(String key, String value) {
        qParams.add(new BasicNameValuePair(key, value));
        return this;
    }
}
