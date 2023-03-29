package com.moon.core.util;


/**
 * @author ZhangDongMin
 */
@FunctionalInterface
public interface Unmodifiable<T> {
    /**
     * 调整为不可修改
     *
     * @return
     */
    Unmodifiable<T> flipToUnmodify();
}
