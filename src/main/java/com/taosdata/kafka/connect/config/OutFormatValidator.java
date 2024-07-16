package com.taosdata.kafka.connect.config;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class OutFormatValidator implements ConfigDef.Validator{

    public static final OutFormatValidator INSTANCE = new OutFormatValidator();

    @Override
    public void ensureValid(String s, Object o) {
        Set<String> set = Arrays.stream(new String[]{"json", "line"}).collect(Collectors.toSet());
        if (!set.contains(String.valueOf(o).toLowerCase())){
            throw new ConfigException(s, o, "outFormat config must one of telnet/json");
        }
    }
}
