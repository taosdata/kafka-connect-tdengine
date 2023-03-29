package com.moon.runner.core;

/**
 * @author moonsky
 */
class DataNum extends DataConst {
    private DataNum(Number value) { super(value); }

    @Override
    public boolean isNumber() { return true; }

    final static AsConst valueOf(Number str) {
        AsConst CONST = getValue(str);
        if (CONST == null) {
            CONST = putValue(str, new DataNum(str));
        }
        return CONST;
    }

    final static AsConst tempNum(Number str) { return new DataNum(str); }
}
