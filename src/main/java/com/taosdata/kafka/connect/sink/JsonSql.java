package com.taosdata.kafka.connect.sink;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.stream.Collectors;

public class JsonSql {
    private String stName;
    private String tName;
    private Map<String, String> cols = Maps.newHashMap();
    private Map<String, String> tag = Maps.newHashMap();
    private Map<String, String> all = Maps.newHashMap();

    public String getStName() {
        return stName;
    }

    public void setStName(String stName) {
        this.stName = stName;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public Map<String, String> getCols() {
        return cols;
    }

    public void setCols(Map<String, String> cols) {
        this.cols = cols;
    }

    public Map<String, String> getTag() {
        return tag;
    }

    public void setTag(Map<String, String> tag) {
        this.tag = tag;
    }

    public Map<String, String> getAll() {
        return all;
    }

    public void setAll(Map<String, String> all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return "JsonSql{" +
                "stName='" + stName + '\'' +
                ", tName='" + tName + '\'' +
                ", cols=" + cols.entrySet().stream().map(e -> "key:" + e.getKey() + ", value: " + e.getValue()).collect(Collectors.joining("-----")) +
                ", tag=" + tag.entrySet().stream().map(e -> "key:" + e.getKey() + ", value: " + e.getValue()).collect(Collectors.joining("*******")) +
                ", all=" + all +
                '}';
    }
}
