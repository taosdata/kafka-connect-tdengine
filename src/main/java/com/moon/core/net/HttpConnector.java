package com.moon.core.net;

import com.moon.core.lang.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
public class HttpConnector {

    private final String domain;
    private volatile Map<String, String> headers;

    public HttpConnector(String domain) { this.domain = domain; }

    private Map<String, String> ensureHeadersMap() {
        Map<String, String> headersMap = this.headers;
        if (headersMap == null) {
            headersMap = new HashMap<>();
            this.headers = headersMap;
        }
        return headersMap;
    }

    public static HttpConnector of(String domain) {
        return new HttpConnector(domain);
    }

    public HttpConnector setHeader(String name, String value) {
        Map<String, String> headersMap = ensureHeadersMap();
        if (StringUtil.isEmpty(value)) {
            headersMap.remove(name);
        } else {
            headersMap.put(name, value);
        }
        return this;
    }

    public HttpConnector removeHeader(String name) {
        ensureHeadersMap().remove(name);
        return this;
    }

}
