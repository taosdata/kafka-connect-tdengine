package com.taosdata.kafka.connect.enums;

/**
 * @author huolibo@qq.com
 * @version v1.0.0
 * @JDK: 1.8
 * @description: connection url prefix enum
 * @date 2021-11-04 14:12
 */
public enum ConnectionTypeEnum {
    TAOS("jdbc:TAOS"),
//    TAOS_RS("jdbc:TAOS-RS"),
//    JNI("jni");
    ;
    private String protocolPrefix;

    ConnectionTypeEnum(String protocolPrefix) {
        this.protocolPrefix = protocolPrefix;
    }

    /**
     * check url is correct to create tdengine connection
     * @param url
     * @return
     */
    public static boolean isValidTDengineUrl(String url){
        if (null == url || url.trim().length() <= 0) {
            return false;
        }

        for (ConnectionTypeEnum connection : ConnectionTypeEnum.values()) {
            if (url.startsWith(connection.protocolPrefix)) {
                return true;
            }
        }
        return false;
    }

}
