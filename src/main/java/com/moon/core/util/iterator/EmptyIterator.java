package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author ZhangDongMin
 */
@SuppressWarnings("rawtypes")
public enum EmptyIterator implements Iterator {
    /** 默认值 */
    DEFAULT;
    /**
     * 为什么要定义这个变量？
     * <p>
     * 因为 EMPTY 这个变量名在具体使用的时候可能已经被定义了
     */
    public static final EmptyIterator VALUE = DEFAULT;
    public static final EmptyIterator empty = VALUE;
    public static final EmptyIterator EMPTY = empty;

    @Override
    public final boolean hasNext() { return false; }

    @Override
    public final Object next() { throw new UnsupportedOperationException(); }
}
