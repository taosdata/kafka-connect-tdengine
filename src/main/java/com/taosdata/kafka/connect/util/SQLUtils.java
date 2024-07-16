package com.taosdata.kafka.connect.util;

/**
 * create sql statement
 */
public class SQLUtils {
    private SQLUtils() {
    }

    public static String showTableSql(String dbName) {
        return "select table_name from information_schema.ins_tables where db_name = '"
                + dbName + "' and stable_name is null";
    }

    public static String showSTableSql(String dbName) {
        return "select stable_name from information_schema.ins_stables where db_name = '" + dbName + "'";
    }

    public static String describeTableSql(String tbName) {
        return "describe " + tbName;
    }
}
