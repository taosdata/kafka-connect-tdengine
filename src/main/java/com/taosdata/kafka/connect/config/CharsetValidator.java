package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.nio.charset.Charset;
import java.util.List;

/**
 * charset validator
 */
public class CharsetValidator implements ConfigDef.Validator {
    public static final CharsetValidator INSTANCE = new CharsetValidator();

    @Override
    public void ensureValid(String config, Object value) {
        if (value instanceof String) {
            validate(config, (String) value);
        } else if (value instanceof List) {
            List<String> values = (List<String>) value;
            for (String v : values) {
                validate(config, v);
            }
        } else {
            throw new ConfigException(config, value, "Must be a string or list.");
        }
    }

    static void validate(String config, String value) {
        try {
            Charset.forName(value);
        } catch (Exception ex) {
            ConfigException configException = new ConfigException(
                    config, value, "Charset is invalid."
            );
            configException.initCause(ex);

            throw configException;
        }
    }
}
