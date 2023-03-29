package com.moon.core.model.converter;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface DTOConverter<T> extends Converter {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toDTO();
}
