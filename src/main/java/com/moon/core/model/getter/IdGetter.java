package com.moon.core.model.getter;

import com.moon.core.model.supplier.IdSupplier;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface IdGetter extends IdSupplier<String>, Getter {

    /**
     * get id
     *
     * @return
     */
    @Override
    String getId();
}
