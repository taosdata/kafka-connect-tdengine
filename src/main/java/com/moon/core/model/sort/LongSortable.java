package com.moon.core.model.sort;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface LongSortable extends Sortable<Long> {

    /**
     * 序号
     *
     * @return
     */
    @Override
    Long getSortValue();
}
