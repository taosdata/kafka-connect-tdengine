package com.taosdata.kafka.connect.source;

import com.taosdata.jdbc.TSDBDriver;
import com.taosdata.jdbc.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UrlParser {
    public static Map<String, String> parse(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        Map<String, String> urlProps = new HashMap<>();
        int beginningOfSlashes = url.indexOf("//");

        String dbProductName = url.substring(0, beginningOfSlashes);
        dbProductName = dbProductName.substring(dbProductName.indexOf(":") + 1);
        dbProductName = dbProductName.substring(0, dbProductName.indexOf(":"));
        urlProps.put(TSDBDriver.PROPERTY_KEY_PRODUCT_NAME, dbProductName);
        // parse dbname
        url = url.substring(beginningOfSlashes + 2);
        int indexOfSlash = url.indexOf("/");
        if (indexOfSlash != -1) {
            if (indexOfSlash + 1 < url.length()) {
                urlProps.put(TSDBDriver.PROPERTY_KEY_DBNAME, url.substring(indexOfSlash + 1).toLowerCase());
            }
            url = url.substring(0, indexOfSlash);
        }
        // parse port
        int indexOfColon = url.indexOf(":");
        if (indexOfColon != -1) {
            if (indexOfColon + 1 < url.length()) {
                urlProps.put(TSDBDriver.PROPERTY_KEY_PORT, url.substring(indexOfColon + 1));
            }
            url = url.substring(0, indexOfColon);
        }
        // parse host
        if (url.length() > 0 && url.trim().length() > 0) {
            urlProps.put(TSDBDriver.PROPERTY_KEY_HOST, url);
        }
        return urlProps;
    }
}
