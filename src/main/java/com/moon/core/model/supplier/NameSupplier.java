package com.moon.core.model.supplier;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface NameSupplier<T> {

    /**
     * 获取 name
     *
     * @return name
     */
    T getName();

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    default Supplier<T> asNameSupplier() { return this::getName; }
}
