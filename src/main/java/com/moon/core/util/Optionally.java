package com.moon.core.util;

/**
 * @author moonsky
 */
public interface Optionally {
    /**
     * 是否存在
     *
     * @return 是否存在
     */
    boolean isPresent();

    /**
     * 是否缺失
     *
     * @return 是否不存在
     */
    default boolean isAbsent() { return !isPresent(); }
}
