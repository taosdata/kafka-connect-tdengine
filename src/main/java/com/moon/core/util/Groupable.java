package com.moon.core.util;

/**
 * 可分组的
 *
 * @author moonsky
 */
@FunctionalInterface
public interface Groupable<K> {

    /**
     * 返回分组的键
     *
     * @return 键
     */
    K group();
}
