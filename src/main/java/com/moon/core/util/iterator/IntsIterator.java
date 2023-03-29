package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class IntsIterator
    extends BaseArrayIterator
    implements Iterator<Integer> {

    private final int[] array;

    public IntsIterator(int[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Integer> of(int... values) {
        return values == null ? EMPTY : new IntsIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Integer next() { return this.array[index++]; }
}
