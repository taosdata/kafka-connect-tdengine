package com.moon.core.util.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * 集合倒序迭代器
 *
 * @author moonsky
 */
public class ReverseIterator<E> implements Iterator<E> {

    private final ListIterator<E> iterator;

    public ReverseIterator(Collection<? extends E> collect) { this(new ArrayList(collect).listIterator()); }

    private ReverseIterator(ListIterator<E> iterator) {this.iterator = iterator;}

    public static <T> Iterator<T> of(Collection<? extends T> collect) {
        return collect == null ? EmptyIterator.EMPTY : new ReverseIterator<>(collect);
    }

    @Override
    public boolean hasNext() { return iterator.hasPrevious(); }

    @Override
    public E next() { return iterator.previous(); }
}
