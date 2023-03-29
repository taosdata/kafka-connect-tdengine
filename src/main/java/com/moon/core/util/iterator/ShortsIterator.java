package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class ShortsIterator
    extends BaseArrayIterator
    implements Iterator<Short> {

    private final short[] array;

    public ShortsIterator(short[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Short> of(short... values) {
        return values == null ? EMPTY : new ShortsIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Short next() { return this.array[index++]; }
}
