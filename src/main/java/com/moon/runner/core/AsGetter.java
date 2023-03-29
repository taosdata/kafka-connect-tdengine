package com.moon.runner.core;

import java.util.function.Predicate;

/**
 * @author moonsky
 */
interface AsGetter extends AsValuer, Predicate {

    /**
     * 是否使用外部数据
     *
     * @return
     */
    @Override
    default boolean isGetter() { return true; }

    /**
     * 是一个简单获取器
     *
     * @return
     */
    default boolean isGetterOrdinary() { return false; }

    /**
     * 是一个复杂获取器
     *
     * @return
     */
    default boolean isGetterComplex() { return false; }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o
     * @return
     */
    @Override
    default boolean test(Object o) { return false; }
}
