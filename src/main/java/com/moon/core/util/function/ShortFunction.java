package com.moon.core.util.function;

/**
 * @author benshaoye
 */
public interface ShortFunction<R> {
    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    R apply(short value);
}
