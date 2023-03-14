package com.taosdata.kafka.connect.sink;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class Schema {
    public static final String SEPARATOR = "\\.";

    private String name;
    private String database;
    private String stableName;
    private String defaultStable;
    private Map<String, StableCondition> condition;
    private Map<String, StableSchema> stableSchemaMap = Maps.newHashMap();

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

    public String getStableName() {
        return stableName;
    }

    public void setStableName(String stableName) {
        this.stableName = stableName;
    }

    public String getDefaultStable() {
        return defaultStable;
    }

    public void setDefaultStable(String defaultStable) {
        this.defaultStable = defaultStable;
    }

    public Map<String, StableCondition> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, StableCondition> condition) {
        this.condition = condition;
    }

    public Map<String, StableSchema> getStableSchemaMap() {
        return stableSchemaMap;
    }

    public void setStableSchemaMap(Map<String, StableSchema> stableSchemaMap) {
        this.stableSchemaMap = stableSchemaMap;
    }
}
