package com.moon.core.lang;

import com.moon.core.enums.Const;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * @author moonsky
 */
public final class JoinerUtil {

    /**
     * 经过一定量测试，业务中大多数字符串长度在 9 ~ 18 个字符之间，样本平均值是 14.6
     */
    public static final int DFT_LEN = 16;
    static final String NULL_STR = "null";
    public final static String EMPTY = Const.EMPTY;

    /**
     * 英文逗号：","
     */
    protected static final String DFT_SEP = String.valueOf((char) 44);

    private JoinerUtil() { ThrowUtil.noInstanceError(); }

    /*
     * ---------------------------------------------------------------------------
     * string joiner.
     * ---------------------------------------------------------------------------
     */

    public static Joiner of() { return of(DFT_SEP); }

    public static Joiner of(CharSequence delimiter) { return Joiner.of(delimiter); }

    public static Joiner of(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return Joiner.of(delimiter, prefix, suffix);
    }

    /*
     * ---------------------------------------------------------------------------
     * basic type array joiner.
     * ---------------------------------------------------------------------------
     */

    public static String join(boolean[] array) { return join(array, DFT_SEP); }

    public static String join(boolean[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                if (separator == null) {
                    separator = NULL_STR;
                }
                int sepLen = separator.length();
                int newLen = 5 + (5 + sepLen) * (len - 1);

                char[] data = new char[newLen];
                int pos = ParseSupportUtil.addBooleanValue(data, array[0], 0);
                for (int i = 1; i < len; i++) {
                    separator.getChars(0, sepLen, data, pos);
                    pos = ParseSupportUtil.addBooleanValue(data, array[i], pos + sepLen);
                }
                return String.valueOf(data, 0, pos);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(char[] array) { return join(array, DFT_SEP); }

    public static String join(char[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                if (separator == null) {
                    separator = NULL_STR;
                }
                int length = separator.length();
                if (length == 0) {
                    return new String(array);
                }
                int size = len + (len - 1) * length;
                char[] ret = new char[size];
                int descBegin = 0;
                ret[descBegin] = array[descBegin];
                for (int i = 1; i < len; i++) {
                    separator.getChars(0, length, ret, ++descBegin);
                    ret[descBegin += length] = array[i];
                }
                return new String(ret);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(byte[] array) { return join(array, DFT_SEP); }

    public static String join(byte[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(short[] array) { return join(array, DFT_SEP); }

    public static String join(short[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(int[] array) { return join(array, DFT_SEP); }

    public static String join(int[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(long[] array) { return join(array, DFT_SEP); }

    public static String join(long[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(float[] array) { return join(array, DFT_SEP); }

    public static String join(float[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(double[] array) { return join(array, DFT_SEP); }

    public static String join(double[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * Object array joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(T[] array) { return join(array, DFT_SEP); }

    public static <T> String join(T[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * collection joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(Collection<T> collection) { return join(collection, DFT_SEP); }

    public static <T> String join(Collection<T> collection, String separator) {
        if (collection != null) {
            int size = collection.size();
            if (size > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + DFT_LEN) * size);
                for (T item : collection) {
                    sb.append(separator).append(item);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * iterable joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(Iterable<T> iterable) { return join(iterable, DFT_SEP); }

    public static <T> String join(Iterable<T> iterable, String separator) {
        if (iterable != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            for (T item : iterable) {
                joiner.append(separator).append(item);
            }
            return joiner.substring(separatorLength(separator));
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * iterator joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(Iterator<T> iterator) { return join(iterator, DFT_SEP); }

    public static <T> String join(Iterator<T> iterator, String separator) {
        if (iterator != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            while (iterator.hasNext()) {
                joiner.append(separator).append(iterator.next());
            }
            return joiner.substring(separatorLength(separator));
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * inner methods
     * ---------------------------------------------------------------------------
     */

    private final static StringBuilder joinerBySeparator(String separator) {
        return new StringBuilder((separatorLength(requireNonNull(separator)) + DFT_LEN) * 16);
    }

    private static int separatorLength(CharSequence cs) { return cs == null ? 4 : cs.length(); }

    static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}
