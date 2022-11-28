package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class SchemaValidator implements ConfigDef.Validator {
    public static final SchemaValidator INSTANCE = new SchemaValidator();

    @Override
    public void ensureValid(String name, Object value) {
        if (value == null || "".endsWith(String.valueOf(value).trim())) {
            throw new ConfigException(name, value,
                    "JSON schema undefined");
        }
    }
}
