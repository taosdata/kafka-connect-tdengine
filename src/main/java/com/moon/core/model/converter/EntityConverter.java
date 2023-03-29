package com.moon.core.model.converter;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface EntityConverter<T> extends Converter {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toEntity();
}
