package com.taosdata.kafka.connect.sink;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

public class Index {
    private String name;

    private Column column;

    private Map<String, Index> indexMap = Maps.newHashMap();

    public Index get(String key) {
        if (Strings.isNullOrEmpty(key)) {
            return null;
        }
        if (indexMap.containsKey(key)) {
            return indexMap.get(key);
        }
        return null;
    }

    public Index(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Index> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(Map<String, Index> indexMap) {
        this.indexMap = indexMap;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }
}
