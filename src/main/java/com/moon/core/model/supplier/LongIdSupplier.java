package com.moon.core.model.supplier;

/**
 * @author moonsky
 */
public interface LongIdSupplier extends IdSupplier<Long> {

    /**
     * get an id
     *
     * @return
     */
    @Override
    Long getId();
}
