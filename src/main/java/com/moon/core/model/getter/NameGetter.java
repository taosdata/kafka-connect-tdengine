package com.moon.core.model.getter;

import com.moon.core.model.supplier.NameSupplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface NameGetter extends NameSupplier<String>, Getter {

    /**
     * get name
     *
     * @return
     */
    @Override
    String getName();
}
