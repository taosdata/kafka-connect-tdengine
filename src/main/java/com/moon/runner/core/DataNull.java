package com.moon.runner.core;

/**
 * @author moonsky
 */
enum DataNull implements AsConst {
    NULL;


    @Override
    public boolean isNull() { return true; }

    @Override
    public boolean isObject() { return false; }

    @Override
    public String toString() { return "null"; }

    @Override
    public Object run(Object data) { return null; }
}
