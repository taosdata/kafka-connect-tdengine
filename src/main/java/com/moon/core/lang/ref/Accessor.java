package com.moon.core.lang.ref;

import com.moon.core.util.Optionally;

/**
 * @author moonsky
 */
public interface Accessor<T, IMPL extends Accessor<T, IMPL>> extends Optionally {

    /**
     * 是否存在
     *
     * @return
     */
    @Override
    boolean isPresent();

    /**
     * 是否缺失
     *
     * @return
     */
    @Override
    default boolean isAbsent() { return false; }

    /**
     * 清空
     *
     * @return
     */
    default IMPL clear() { return current(); }

    /**
     * this 对象
     *
     * @return
     */
    default IMPL current() { return (IMPL) this; }
}
