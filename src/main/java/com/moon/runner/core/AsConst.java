package com.moon.runner.core;

/**
 * @author moonsky
 */
interface AsConst extends AsValuer {

    /**
     * 普通常量
     *
     * @return
     */
    @Override
    default boolean isConst() { return true; }

    /**
     * 是一个字符串常量
     *
     * @return
     */
    default boolean isString() { return false; }

    /**
     * 是一个数值常量
     *
     * @return
     */
    default boolean isNumber() { return false; }

    /**
     * 是一个 boolean 常量
     *
     * @return
     */
    default boolean isBoolean() { return false; }

    /**
     * 是一个 Object 常量
     *
     * @return
     */
    default boolean isObject() { return false; }

    /**
     * null
     *
     * @return
     */
    default boolean isNull() { return false; }
}
