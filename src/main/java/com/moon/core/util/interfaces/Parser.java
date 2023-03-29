package com.moon.core.util.interfaces;

import java.util.function.Function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface Parser<T, S> {

    /**
     * 执行解析
     *
     * @param source 解析目标
     *
     * @return 解析结果
     */
    T parse(S source);

    /**
     * transfer to a {@link Function}
     *
     * @return a new function
     */
    default Function<S, T> asParserFunction() { return this::parse; }
}
