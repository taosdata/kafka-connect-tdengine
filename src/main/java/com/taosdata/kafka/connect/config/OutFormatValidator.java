package com.taosdata.kafka.connect.config;

import com.taosdata.kafka.connect.enums.OutputFormatEnum;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class OutFormatValidator implements ConfigDef.Validator {
    private static final Logger log = LoggerFactory.getLogger(OutFormatValidator.class);

    public static final OutFormatValidator INSTANCE = new OutFormatValidator();

    @Override
    public void ensureValid(String name, Object value) {
        if (!OutputFormatEnum.isValid(String.valueOf(value))) {
            throw new ConfigException(name, value,
                    "outFormat config must one of line/json");
        }
    }
}
