package com.moon.core.util.iterator;

import com.moon.core.lang.StringUtil;

import java.util.Iterator;

/**
 * @author moonsky
 */
public class CharsIterator
    extends BaseArrayIterator
    implements Iterator<Character> {

    private final char[] array;

    public CharsIterator(CharSequence string) { this(StringUtil.toCharArray(string)); }

    public CharsIterator(char[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    public static Iterator<Character> of(CharSequence sequence) {
        return sequence == null ? EMPTY : new CharsIterator(sequence);
    }

    public static Iterator<Character> of(char... values) {
        return values == null ? EMPTY : new CharsIterator(values);
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Character next() { return this.array[index++]; }
}
