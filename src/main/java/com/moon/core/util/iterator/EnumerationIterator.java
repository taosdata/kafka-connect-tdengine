package com.moon.core.util.iterator;

import com.moon.core.lang.ObjectUtil;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author moonsky
 */
public class EnumerationIterator<T> implements Iterator<T> {

    private final Enumeration<T> enumeration;

    enum EmptyEnumeration implements Enumeration {
        VALUE;

        @Override
        public boolean hasMoreElements() { return false; }

        @Override
        public Object nextElement() { throw new UnsupportedOperationException(); }
    }

    public EnumerationIterator(Enumeration<T> enumeration) {
        this.enumeration = ObjectUtil.defaultIfNull(enumeration, EmptyEnumeration.VALUE);
    }

    @Override
    public boolean hasNext() { return enumeration.hasMoreElements(); }

    @Override
    public T next() { return enumeration.nextElement(); }
}
