package com.moon.core.util.interfaces;

import java.util.Iterator;
import java.util.function.Function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface IteratorFunction<C, I> {

    /**
     * 获取一个迭代器
     *
     * @param t 迭代器源对象
     *
     * @return 迭代器
     */
    Iterator<I> iterator(C t);

    /**
     * transfer to a {@link Function}
     *
     * @return a new function
     */
    default Function<C, Iterator<I>> asIteratorFunction() { return this::iterator; }
}
