package com.moon.core.model.getter;

import com.moon.core.model.supplier.KeySupplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface KeyGetter extends KeySupplier<String>, Getter {

    /**
     * get key
     *
     * @return
     */
    @Override
    String getKey();
}
