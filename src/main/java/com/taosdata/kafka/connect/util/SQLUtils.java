package com.taosdata.kafka.connect.util;

/**
 * create sql statement
 */
public class SQLUtils {
    private SQLUtils() {
    }

    public static String showTableSql() {
        return "show tables";
    }

    public static String showSTableSql() {
        return "show stables";
    }

    public static String useTableSql(String dbname) {
        return "use " + dbname;
    }

    public static String describeTableSql(String dbname) {
        return "describe " + dbname;
    }
}
