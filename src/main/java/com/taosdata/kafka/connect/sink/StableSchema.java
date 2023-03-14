package com.taosdata.kafka.connect.sink;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class StableSchema {
    private String[] tableName;
    private String delimiter;

    private List<String> filters;

    private Map<String, Index> indexMap = Maps.newHashMap();

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

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    public Map<String, Index> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(Map<String, Index> indexMap) {
        this.indexMap = indexMap;
    }

}
