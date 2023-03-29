package com.moon.core.util.converter;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface TypeConverter<R> {

    /**
     * Alias for apply
     *
     * @param o
     *
     * @return
     */
    R convertTo(Object o);
}
