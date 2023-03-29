package com.moon.core.util.iterator;

import com.moon.core.exception.DefaultException;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author moonsky
 */
public class ResultSetIterator implements Iterator<ResultSet> {

    private final ResultSet set;

    public ResultSetIterator(ResultSet set) { this.set = Objects.requireNonNull(set); }

    @Override
    public boolean hasNext() {
        try {
            return set.next();
        } catch (Exception e) {
            throw DefaultException.with(e);
        }
    }

    @Override
    public ResultSet next() { return set; }
}
