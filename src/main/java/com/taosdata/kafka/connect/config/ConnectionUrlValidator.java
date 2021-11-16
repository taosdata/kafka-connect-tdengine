package com.taosdata.kafka.connect.config;

import com.taosdata.kafka.connect.enums.ConnectionTypeEnum;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

/**
 * check url is correct to connect with tdengine
 */
public class ConnectionUrlValidator implements ConfigDef.Validator{

    public static final ConnectionUrlValidator INSTANCE = new ConnectionUrlValidator();

    @Override
    public void ensureValid(String name, Object value) {
        if (!ConnectionTypeEnum.isValidTDengineUrl(String.valueOf(value))){
            throw new ConfigException(name, value, "check connection.url is correct");
        }
    }
}
