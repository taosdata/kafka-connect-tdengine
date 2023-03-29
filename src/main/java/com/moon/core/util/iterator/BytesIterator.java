package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class BytesIterator extends BaseArrayIterator implements Iterator<Byte> {

    private final byte[] array;

    public BytesIterator(byte[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Byte> of(byte... values) {
        return values == null ? EMPTY : new BytesIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Byte next() { return this.array[index++]; }
}
