package com.moon.core.model.supplier;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface IdSupplier<T> {
    /**
     * 获取 ID
     *
     * @return
     */
    T getId();
    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    default Supplier<T> asIdSupplier(){ return this::getId; }
}
