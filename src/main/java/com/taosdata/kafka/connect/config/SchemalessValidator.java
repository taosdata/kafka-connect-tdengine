package com.taosdata.kafka.connect.config;

import com.taosdata.jdbc.enums.SchemalessProtocolType;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * check schemaless config
 */
public class SchemalessValidator implements ConfigDef.Validator{
    private static final Logger log = LoggerFactory.getLogger(SchemalessValidator.class);

    public static final SchemalessValidator INSTANCE = new SchemalessValidator();
    @Override
    public void ensureValid(String name, Object value) {
        if (SchemalessProtocolType.UNKNOWN == SchemalessProtocolType.parse(String.valueOf(value))){
            throw new ConfigException(name, value, "schemaless config must one of LINE/TELNET/JSON");
        }
    }
}
