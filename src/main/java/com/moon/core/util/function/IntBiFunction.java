package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface IntBiFunction<T, R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param value
     * @param obj
     * @return
     */
    R apply(int value, T obj);
}
