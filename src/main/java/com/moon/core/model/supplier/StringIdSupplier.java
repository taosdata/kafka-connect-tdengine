package com.moon.core.model.supplier;

/**
 * @author moonsky
 */
public interface StringIdSupplier extends IdSupplier<String> {

    /**
     * get an id
     *
     * @return
     */
    @Override
    String getId();
}
