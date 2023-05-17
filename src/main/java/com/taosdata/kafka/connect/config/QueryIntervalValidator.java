package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class QueryIntervalValidator implements ConfigDef.Validator {

    public static final QueryIntervalValidator INSTANCE = new QueryIntervalValidator();

    @Override
    public void ensureValid(String s, Object o) {
        if (null != o) {
            long queryInterval = Long.parseLong(String.valueOf(o));
            if (queryInterval > 1000 * 60 * 60 * 24 * 31L) {
                throw new ConfigException(s, o, "query interval must not be greater than 1 month");
            }
        }
    }
}
