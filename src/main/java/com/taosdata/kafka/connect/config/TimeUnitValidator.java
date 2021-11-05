package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @description: check timeunit config
 * @date 2021-11-04 14:12
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
