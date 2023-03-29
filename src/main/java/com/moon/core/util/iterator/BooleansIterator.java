package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class BooleansIterator
    extends BaseArrayIterator
    implements Iterator<Boolean> {

    private final boolean[] array;

    public BooleansIterator(boolean[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Boolean> of(boolean... values) {
        return values == null ? EMPTY : new BooleansIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Boolean next() { return this.array[index++]; }
}
