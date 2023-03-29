package com.moon.core.model.getter;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
public interface KeyValueGetter extends KeyGetter, ValueGetter {

    /**
     * get key
     *
     * @return
     */
    @Override
    String getKey();

    /**
     * get value
     *
     * @return
     */
    @Override
    String getValue();

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    @Override
    default Supplier<String> asKeySupplier() { return this::getKey; }

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    @Override
    default Supplier<String> asValueSupplier() { return this::getValue; }
}
