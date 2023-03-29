package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface BiIntFunction<T, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param obj   the function argument
     * @param value the function argument
     *
     * @return result
     */
    R apply(T obj, int value);
}
