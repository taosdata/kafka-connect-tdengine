package com.taosdata.kafka.connect.enums;

import com.taosdata.jdbc.enums.SchemalessTimestampType;
import com.taosdata.jdbc.utils.StringUtils;

public enum DataPrecision {
    MS(SchemalessTimestampType.MILLI_SECONDS),
    US(SchemalessTimestampType.MICRO_SECONDS),
    NS(SchemalessTimestampType.NANO_SECONDS),
    ;

    private final SchemalessTimestampType type;

    DataPrecision(SchemalessTimestampType type) {
        this.type = type;
    }

    public static boolean isValid(String precision) {
        if (StringUtils.isEmpty(precision)){
            return true;
        }
        for (DataPrecision value : DataPrecision.values()) {
            if (value.name().equalsIgnoreCase(precision)) {
                return true;
            }
        }
        return false;
    }

    public static SchemalessTimestampType getTimestampType(String precision) {
        if (StringUtils.isEmpty(precision)) {
            return SchemalessTimestampType.NOT_CONFIGURED;
        }
        for (DataPrecision value : DataPrecision.values()) {
            if (value.name().equalsIgnoreCase(precision)) {
                return value.type;
            }
        }
        return SchemalessTimestampType.NOT_CONFIGURED;
    }
}
