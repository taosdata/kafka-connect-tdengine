package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class ObjectsIterator<T> extends BaseArrayIterator implements Iterator<T> {

    private final T[] array;

    public ObjectsIterator(T[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @SafeVarargs
    public static <T> Iterator<T> of(T... values) {
        return values == null ? EMPTY : new ObjectsIterator<>(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }


    @Override
    public T next() { return this.array[index++]; }
}
