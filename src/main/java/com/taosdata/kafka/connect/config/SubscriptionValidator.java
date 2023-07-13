package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class SubscriptionValidator implements ConfigDef.Validator {

    public static final SubscriptionValidator INSTANCE = new SubscriptionValidator();

    @Override
    public void ensureValid(String name, Object value) {
        String sub = String.valueOf(value).toLowerCase();
        if (!sub.equals("subscription") && !sub.equals("query")) {
            throw new ConfigException(name, value, "read method may be one of subscription or query");
        }
    }
}
