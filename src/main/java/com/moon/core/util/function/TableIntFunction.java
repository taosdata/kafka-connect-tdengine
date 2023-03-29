package com.moon.core.util.function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface TableIntFunction<F, S, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param first  the function argument
     * @param second the function argument
     * @param index  the function argument
     *
     * @return the function result
     */
    R apply(F first, S second, int index);
}
