package com.taosdata.kafka.connect.config;

import com.taosdata.kafka.connect.enums.DataPrecision;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class PrecisionValidator implements ConfigDef.Validator {

    public static final PrecisionValidator INSTANCE = new PrecisionValidator();

    @Override
    public void ensureValid(String name, Object value) {
        if (!DataPrecision.isValid(String.valueOf(value))) {
            throw new ConfigException(name, value,
                    "database precision config must be one of (ms, us, ns)");
        }
    }
}
