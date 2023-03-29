package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface BooleanFunction<R> {
    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    R apply(boolean value);
}
