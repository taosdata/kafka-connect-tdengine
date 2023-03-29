package com.moon.core.net;

import com.moon.core.lang.ThrowUtil;

/**
 * @author moonsky
 */
public final class HttpUtil {

    /**
     * default http request timeout
     */
    final static int TIMEOUT = 60000;

    private HttpUtil() { ThrowUtil.noInstanceError(); }

    public static HttpConnector connector(String url) {
        throw new UnsupportedOperationException();
    }
}
