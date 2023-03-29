package com.moon.core.model.converter;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface VOConverter<T> extends Converter {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toVO();
}
