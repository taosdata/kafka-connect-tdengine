package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface ToShortFunction<T> {

    /**
     * compute as a short value
     *
     * @param data 入参数据
     *
     * @return short value
     */
    short applyAsShort(T data);
}
