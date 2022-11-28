package com.taosdata.kafka.connect.sink;

import java.util.Map;

public class Schema {
    public static final String SEPARATOR = "\\.";

    private String name;
    private String database;
    private String stableNameSpecify;
    private String stableName;
    private String[] tableName;
    private String delimiter;
    private Map<String, Index> indexMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getStableNameSpecify() {
        return stableNameSpecify;
    }

    public void setStableNameSpecify(String stableNameSpecify) {
        this.stableNameSpecify = stableNameSpecify;
    }

    public String getStableName() {
        return stableName;
    }

    public void setStableName(String stableName) {
        this.stableName = stableName;
    }

    public String[] getTableName() {
        return tableName;
    }

    public void setTableName(String[] tableName) {
        this.tableName = tableName;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public Map<String, Index> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(Map<String, Index> indexMap) {
        this.indexMap = indexMap;
    }
}
