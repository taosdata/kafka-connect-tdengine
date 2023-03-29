package com.moon.core.util.interfaces;

import java.util.function.Function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface Stringify<T> {

    /**
     * 字符串化对象
     *
     * @param t 数据
     *
     * @return 字符串结果
     */
    String stringify(T t);

    /**
     * transfer to a {@link Function}
     *
     * @return a new function
     */
    default Function<T, String> asFunction() { return this::stringify; }
}
