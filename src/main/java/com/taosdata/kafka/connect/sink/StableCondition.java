package com.taosdata.kafka.connect.sink;

/**
 * @author taosdata
 */
public class StableCondition {
    private String cmp;
    private String key;
    private Object value;

    public StableCondition(String key, String cmp, Object value) throws Exception {
        this.cmp = cmp;
        if (null == StableCMP.getCMP(cmp)) {
            throw new Exception("Unsupported operation type");
        }
        this.key = key;
        this.value = value;
    }

    public String getCmp() {
        return cmp;
    }

    public void setCmp(String cmp) {
        this.cmp = cmp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
