package com.taosdata.kafka.connect.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class VersionUtils {
    private static final Logger log = LoggerFactory.getLogger(VersionUtils.class);
    private static final String PATH = "/kafka-connect-tdengine.properties";
    private static String version = "unknown";

    static {
        try (InputStream stream = VersionUtils.class.getResourceAsStream(PATH)) {
            Properties props = new Properties();
            props.load(stream);
            version = props.getProperty("version", version).trim();
        } catch (Exception e) {
            log.warn("Error while loading version:", e);
        }
    }

    public static String getVersion() {
        return version;
    }
}
