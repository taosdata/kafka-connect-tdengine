package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * check timeunit config
 */
public class TimeUnitValidator implements ConfigDef.Validator {
    public static final TimeUnitValidator INSTANCE = new TimeUnitValidator();

    @Override
    public void ensureValid(String name, Object value) {
        if (null != value) {
            Arrays.stream(TimeUnit.values()).map(Enum::toString)
                    .filter(s -> s.equalsIgnoreCase(String.valueOf(value))).findAny()
                    .orElseThrow(() -> new ConfigException(name, value, "Must be a timeUnit"));
        }
    }
}
