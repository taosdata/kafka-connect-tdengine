package com.taosdata.kafka.connect.util;

/**
 * create sql statement
 */
public class SQLUtil {
    private SQLUtil() {
    }

    public static String showTableSql() {
        return "show tables";
    }

    public static String useTableSql(String dbname) {
        return "use " + dbname;
    }
}
