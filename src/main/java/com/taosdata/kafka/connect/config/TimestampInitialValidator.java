package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampInitialValidator implements ConfigDef.Validator {

    public static final TimestampInitialValidator INSTANCE = new TimestampInitialValidator();

    @Override
    public void ensureValid(String s, Object o) {
        if (null != o) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(String.valueOf(o), df);
            if (dateTime.isAfter(LocalDateTime.now())) {
                throw new ConfigException(s, o, "timestamp initial value must be before now");
            }
        }
    }
}
