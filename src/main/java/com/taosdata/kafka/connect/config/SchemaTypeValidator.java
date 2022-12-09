package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.taosdata.kafka.connect.sink.SinkConstants.SCHEMA_TYPE_LOCAL;
import static com.taosdata.kafka.connect.sink.SinkConstants.SCHEMA_TYPE_REMOTE;

public class SchemaTypeValidator implements ConfigDef.Validator {
    private static final Logger log = LoggerFactory.getLogger(SchemaTypeValidator.class);

    public static final SchemaTypeValidator INSTANCE = new SchemaTypeValidator();

    @Override
    public void ensureValid(String name, Object value) {
        if (value == null || "".endsWith(String.valueOf(value).trim())) {
            throw new ConfigException(name, value,
                    "JSON schema type could not be null!");
        }
        value = String.valueOf(value).trim().toLowerCase();
        if (!SCHEMA_TYPE_LOCAL.equals(value) && !SCHEMA_TYPE_REMOTE.equals(value)) {
            throw new ConfigException(name, value,
                    "JSON schema type must be one of (local or remote)");
        }

    }
}
