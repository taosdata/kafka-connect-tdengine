package com.moon.core.model.supplier;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface ValueSupplier<T> {

    /**
     * 获取值
     *
     * @return value
     */
    T getValue();

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    default Supplier<T> asValueSupplier() { return this::getValue; }
}
