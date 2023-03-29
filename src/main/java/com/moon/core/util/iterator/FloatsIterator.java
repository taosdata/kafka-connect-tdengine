package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class FloatsIterator
    extends BaseArrayIterator
    implements Iterator<Float> {

    private final float[] array;

    public FloatsIterator(float[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Float> of(float... values) {
        return values == null ? EMPTY : new FloatsIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Float next() { return this.array[index++]; }
}
