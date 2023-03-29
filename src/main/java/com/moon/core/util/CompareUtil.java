package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.*;

/**
 * @author moonsky
 */
public final class CompareUtil {

    private final static int MAX = 2 << 16;

    private CompareUtil() { ThrowUtil.noInstanceError(); }

    public static <T extends Comparable<T>> T max(T... values) {
        int length = values == null ? 0 : values.length;
        switch (length) {
            case 0:
                return null;
            case 1:
                return values[0];
            case 2:
                return values[0].compareTo(values[1]) > 0 ? values[0] : values[1];
            default:
                values = Arrays.copyOf(values, length);
                if (length > MAX) {
                    Arrays.parallelSort(values);
                } else {
                    Arrays.sort(values);
                }
                return values[length - 1];
        }
    }

    public static <T extends Comparable<T>> T min(T... values) {
        int length = values == null ? 0 : values.length;
        switch (length) {
            case 0:
                return null;
            case 1:
                return values[0];
            case 2:
                return values[0].compareTo(values[1]) > 0 ? values[1] : values[0];
            default:
                values = Arrays.copyOf(values, length);
                if (length > MAX) {
                    Arrays.parallelSort(values);
                } else {
                    Arrays.sort(values);
                }
                return values[0];
        }
    }

    public static <T extends Comparable<T>> T min(Collection<T> values) {
        return min(values, Comparator.naturalOrder());
    }

    public static <T extends Comparable<T>> T max(Collection<T> values) {
        return max(values, Comparator.naturalOrder());
    }

    public static <T extends Comparable<T>> T max(Collection<T> values, Comparator comparator) {
        return doCompare(values, comparator, s -> s - 1);
    }

    public static <T extends Comparable<T>> T min(Collection<T> values, Comparator comparator) {
        return doCompare(values, comparator, s -> 0);
    }

    private static <T extends Comparable<T>> T doCompare(Collection<T> values, Comparator comparator, Indexer indexer) {
        int length = values == null ? 0 : values.size();
        if (length < 1) {
            return null;
        }
        List<T> list = new ArrayList<>(values);
        list.sort(comparator);
        return list.get(indexer.get(length));
    }

    interface Indexer {

        int get(int length);
    }
}
