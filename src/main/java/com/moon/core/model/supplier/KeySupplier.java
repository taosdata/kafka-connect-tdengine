package com.moon.core.model.supplier;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface KeySupplier<T> {

    /**
     * 获取 KEY
     *
     * @return key
     */
    T getKey();

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    default Supplier<T> asKeySupplier() { return this::getKey; }
}
