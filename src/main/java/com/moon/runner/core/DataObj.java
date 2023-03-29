package com.moon.runner.core;

/**
 * @author moonsky
 */
class DataObj extends DataConst {
    private DataObj(Object value) { super(value); }

    @Override
    public boolean isObject() { return true; }

    final static AsConst valueOf(Object str) {
        AsConst CONST = getValue(str);
        if (CONST == null) {
            CONST = putValue(str, new DataObj(str));
        }
        return CONST;
    }

    final static AsConst tempObj(Object str) { return new DataObj(str); }
}
