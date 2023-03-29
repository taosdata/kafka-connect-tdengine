package com.moon.core.model.getter;

import com.moon.core.model.supplier.ValueSupplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface ValueGetter extends ValueSupplier<String>, Getter {

    /**
     * get value
     *
     * @return
     */
    @Override
    String getValue();
}
