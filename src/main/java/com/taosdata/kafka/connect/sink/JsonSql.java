package com.taosdata.kafka.connect.sink;

import java.util.Map;

public class JsonSql {
    private String stName;
    private String tName;
    private String ts;
    private Map<String, String> cols;
    private Map<String, String> tags;

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "JsonSql{" +
                "stName='" + stName + '\'' +
                ", tName='" + tName + '\'' +
                ", ts='" + ts + '\'' +
                ", cols=" + cols +
                ", tags=" + tags +
                '}';
    }
}
