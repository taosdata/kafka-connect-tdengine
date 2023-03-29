package com.moon.core.util.function;

import java.util.function.Function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface NullableFunction<T,R> extends Function<T,R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R nullable(T t);

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    @Override
    default R apply(T t) { return nullable(t); }
}
