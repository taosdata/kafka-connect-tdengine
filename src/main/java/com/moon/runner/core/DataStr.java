package com.moon.runner.core;

/**
 * @author moonsky
 */
class DataStr extends DataConst {
    private DataStr(Object value) { super(value); }

    @Override
    public boolean isString() { return true; }

    final static AsConst valueOf(String str) {
        AsConst CONST = getValue(str);
        if (CONST == null) {
            CONST = putValue(str, new DataStr(str));
        }
        return CONST;
    }

    final static AsConst tempStr(String str) { return new DataStr(str); }
}
