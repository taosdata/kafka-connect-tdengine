package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.TimeZone;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @description: check timezone configuration
 * @date 2021-11-04 14:12
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
