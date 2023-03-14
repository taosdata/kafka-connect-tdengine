package com.taosdata.kafka.connect.sink;

/**
 * @author taosdata
 */
public enum StableCMP {
    EQ("="), NE("<>"), IN("in");
    private String cmp;

    StableCMP(String cmp) {
        this.cmp = cmp;
    }

    public String getValue() {
        return this.cmp;
    }

    public static StableCMP getCMP(String val) {
        for (StableCMP cmp: StableCMP.values()) {
            if (cmp.getValue().equals(val)) {
                return cmp;
            }
        }
        return null;
    }
}
