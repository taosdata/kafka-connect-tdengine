package com.taosdata.kafka.connect.enums;

public enum OutputFormatEnum {
    JSON,
    LINE;

    public static boolean isValid(String param) {
        for (OutputFormatEnum value : OutputFormatEnum.values()) {
            if (value.name().equalsIgnoreCase(param)) {
                return true;
            }
        }
        return false;
    }
}
