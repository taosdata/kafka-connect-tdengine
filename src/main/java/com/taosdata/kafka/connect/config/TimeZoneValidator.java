package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.TimeZone;

/**
 * check timezone configuration
 */
public class TimeZoneValidator implements ConfigDef.Validator{

    public static final TimeZoneValidator INSTANCE = new TimeZoneValidator();

    @Override
    public void ensureValid(String name, Object value) {
        if (null != value){
            String timeZone = Arrays.stream(TimeZone.getAvailableIDs())
                    .filter(zone -> zone.startsWith(String.valueOf(value)))
                    .findAny()
                    .orElse(null);
            if (null == timeZone) {
                throw new ConfigException(name, value, "Invalid time zone identifier");
            }
        }
    }
}
