package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class DoublesIterator
    extends BaseArrayIterator
    implements Iterator<Double> {

    private final double[] array;

    public DoublesIterator(double[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Double> of(double... values) {
        return values == null ? EMPTY : new DoublesIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Double next() { return this.array[index++]; }
}
