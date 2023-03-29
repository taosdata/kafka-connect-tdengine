package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class LongsIterator
    extends BaseArrayIterator
    implements Iterator<Long> {

    private final long[] array;

    public LongsIterator(long[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Long> of(long... values) {
        return values == null ? EMPTY : new LongsIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Long next() { return this.array[index++]; }
}
