package io.ticktok.client.server.rest;

import io.ticktok.client.TicktokOptions;
import java.util.LinkedHashMap;
import java.util.Map;

public class UrlResolver {

    public static final String CLOCK_ROOT = "api/v1/clocks";
    private StringBuilder resolved = new StringBuilder();
    private Map<String, String> qParams = new LinkedHashMap<>();
    private TicktokOptions options;

    public UrlResolver(TicktokOptions options){
        this.options = options;
        bootstrapResolver();
    }

    private void bootstrapResolver() {
        resolved.append(options.getDomain()).append(shouldAppendBackSlash(options.getDomain()) ? "/" : "").append(CLOCK_ROOT);
    }

    private boolean shouldAppendBackSlash(String domain) {
        return !domain.substring(domain.length() - 1).equals("/");
    }

    //TODO: I think UriBuilder will do it for you
    public String resolve() {
        StringBuilder url = resolved.append("?").append("access_token=").append(this.options.getToken());
        qParams.forEach((k, v) -> url.append("&").append(k).append("=").append(v));
        String result = url.toString();
        resetQparams();
        resetResolved();
        return result;
    }

    private void resetQparams() {
        qParams = new LinkedHashMap<>();
    }

    private void resetResolved() {
        resolved = new StringBuilder();
        bootstrapResolver();
    }

    public UrlResolver pathParam(String s) {
        resolved.append("/").append(s);
        return this;
    }

    public UrlResolver queryParam(String key, String value) {
        qParams.put(key, value);
        return this;
    }
}
