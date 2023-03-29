package com.moon.runner.core;

import com.moon.runner.Runner;

/**
 * @author moonsky
 */
@FunctionalInterface
interface AsRunner<T> extends Runner<T> {

    /**
     * 使用外部数据
     *
     * @param data 执行数据
     *
     * @return 执行后的结果
     */
    @Override
    default T run(Object data) { throw new UnsupportedOperationException(); }

    /**
     * 计算
     *
     * @param left  左值
     * @param right 右值
     *
     * @return 执行后的结果
     */
    default Object exe(Object right, Object left) { throw new UnsupportedOperationException(); }

    /**
     * 计算
     *
     * @param right 右值
     * @param left  左值
     * @param data  参数
     *
     * @return 执行后的结果
     */
    Object exe(AsRunner right, AsRunner left, Object data);

    /**
     * 运算符优先级
     *
     * @return 运算符优先级
     */
    default int getPriority() { return 99; }

    /*
     * --------------------------------------
     * 判断
     * --------------------------------------
     */

    /**
     * 计算器
     *
     * @return 是否是计算器
     */
    default boolean isHandler() { return false; }

    /**
     * 取值器
     *
     * @return 取值器
     */
    default boolean isValuer() { return isConst() || isGetter(); }

    /**
     * 普通常量
     *
     * @return 是否普通常量
     */
    default boolean isConst() { return false; }

    /**
     * 是否使用外部数据
     *
     * @return 是否使用外部数据
     */
    default boolean isGetter() { return false; }

    /**
     * 是否是赋值器
     *
     * @return 是否是赋值器
     */
    default boolean isSetter() { return false; }

    /**
     * 是否是一个方法执行
     *
     * @return 是否是一个方法执行
     */
    default boolean isInvoker() { return false; }
}
