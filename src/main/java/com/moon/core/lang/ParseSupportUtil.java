package com.moon.core.lang;

import com.moon.core.enums.Arrays2;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.Optional;
import com.moon.core.util.TypeofOptional;
import com.moon.core.util.function.NullableFunction;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.Character.isWhitespace;

/**
 * 本类不推荐任何形式调用
 *
 * @author moonsky
 */
public final class ParseSupportUtil {

    private final static char DOT = '.';

    private ParseSupportUtil() { noInstanceError("本类不推荐任何形式调用"); }

    /**
     * 添加 true
     *
     * @param data
     * @param posBegin
     *
     * @return
     */
    public static int addTrueValue(char[] data, int posBegin) {
        data[posBegin++] = 't';
        data[posBegin++] = 'r';
        data[posBegin++] = 'u';
        data[posBegin++] = 'e';
        return posBegin;
    }

    public static int addFalseValue(char[] data, int posBegin) {
        data[posBegin++] = 'f';
        data[posBegin++] = 'a';
        data[posBegin++] = 'l';
        data[posBegin++] = 's';
        data[posBegin++] = 'e';
        return posBegin;
    }

    public static int addBooleanValue(char[] data, boolean bool, int posBegin) {
        return bool ? addTrueValue(data, posBegin) : addFalseValue(data, posBegin);
    }

    public static int addNullValue(char[] data, int posBegin) {
        data[posBegin++] = 'n';
        data[posBegin++] = 'u';
        data[posBegin++] = 'l';
        data[posBegin++] = 'l';
        return posBegin;
    }

    public static int addUndefinedValue(char[] data, int posBegin) {
        data[posBegin++] = 'u';
        data[posBegin++] = 'n';
        data[posBegin++] = 'd';
        data[posBegin++] = 'e';
        data[posBegin++] = 'f';
        data[posBegin++] = 'i';
        data[posBegin++] = 'n';
        data[posBegin++] = 'e';
        data[posBegin++] = 'd';
        return posBegin;
    }

    /**
     * 解析普通变量
     * 变量：由数字、字母、汉字、$、_ 构成，切不能以数字开头
     * <p>
     * 这里只是承担解析任务，第一个字符有调用识别后传入，
     * <p>
     * 默认第一个字符也是变量组成部分，并且没有判断
     *
     * @param chars   所有字符
     * @param indexer 第一个字符（current）的下一个字符在 chars 中的索引位置
     * @param len     chars 的长度
     * @param current 第一个字符
     *
     * @return
     */
    public final static String parseVar(char[] chars, IntAccessor indexer, int len, int current) {
        char curr = (char) current;
        char[] value = {curr};
        int index = value.length, i = indexer.get();
        for (; i < len && (isVar(curr = chars[i]) || isNum(curr)); i++) {
            value = setChar(value, index++, curr);
        }
        setIndexer(indexer, i, len);
        return toStr(value, index);
    }

    /**
     * 解析字符串中的数字
     * 数字间为了方便阅读，可用下划线分割，允许数字间有一个或多个下划线
     * 表示一个数字的字符串所有字符必须连续
     *
     * @param chars   源字符数组
     * @param indexer 指向数字的第二个字符，如：1234  则索引值为 1，指向字符串中的字符 2，
     * @param len     源数组长度
     * @param current 当前数字字符，如：1234，则为 1
     *
     * @return number
     */
    public final static Number parseNum(char[] chars, IntAccessor indexer, int len, int current) {
        char curr = (char) current;
        char[] value = {curr};
        int index = value.length, i = indexer.get();
        final Number number;
        for (; i < len && (isNum(curr = chars[i]) || isUnderscore(curr)); i++) {
            if (isNum(curr)) {
                value = setChar(value, index++, curr);
            }
        }
        if (curr == DOT) {
            final int cacheIndex = index, cacheI = i + 1;
            value = setChar(value, index++, curr);
            for (++i; i < len && (isNum(curr = chars[i]) || isUnderscore(curr)); i++) {
                if (isNum(curr)) {
                    value = setChar(value, index++, curr);
                }
            }
            if (cacheI == i && isVar(curr)) {
                i--;
                number = Integer.valueOf(toStr(value, cacheIndex));
            } else {
                number = Double.parseDouble(toStr(value, index));
            }
        } else {
            number = Integer.valueOf(toStr(value, index));
        }
        setIndexer(indexer, i, len);
        return number;
    }

    public final static String parseStr(char[] chars, IntAccessor indexer, int endChar) {
        char[] value = Arrays2.CHARS.empty();
        int index = 0, pr = -1, es = '\\', i = indexer.get();
        for (char ch; (ch = chars[i++]) != endChar || pr == es; pr = ch) {
            if (ch == endChar && pr == es) {
                value[index - 1] = ch;
            } else {
                value = setChar(value, index++, ch);
            }
        }
        indexer.set(i);
        return toStr(value, index);
    }

    public final static void setIndexer(IntAccessor indexer, int index, int len) {
        indexer.set(index < len ? index : len);
    }

    public final static String toStr(char[] chars, int len) { return new String(chars, 0, len); }

    public final static boolean isNum(int value) { return value > 47 && value < 58; }

    public final static boolean isUnderscore(int value) { return value == '_'; }

    public final static boolean isVar(int value) {
        return CharUtil.isLetter(value) || value == '$' || isUnderscore(value) || CharUtil.isChinese(value);
    }

    /**
     * 跳过空字符
     *
     * @param chars   字符数组
     * @param indexer 索引器
     * @param len     字符数组长度
     *
     * @return 下一个非空白字符
     */
    public final static int skipWhitespaces(char[] chars, IntAccessor indexer, final int len) {
        int index = indexer.get(), ch = 0;
        while (index < len && isWhitespace(ch = chars[index++])) { }
        indexer.set(index);
        return ch;
    }

    /**
     * 抛出指定位置异常，异常信息为 indexer 指向的前后 amount 个字符，超出首尾自动以首尾为止
     *
     * @param chars   字符数组
     * @param indexer 索引器
     * @param <T>     无
     *
     * @return 无
     */
    public final static <T> T throwErr(char[] chars, IntAccessor indexer) {
        return throwErr(null, chars, indexer);
    }

    public final static <T> T throwErr(String message, char[] chars, IntAccessor indexer) {
        message = message == null ? "" : message;
        int amount = 12, len = chars.length, index = indexer.get(), msgLen = message.length();
        int end = index + amount < len ? index + amount : len;
        int start = index < amount ? 0 : index - amount;
        int maxLen = end - start, preLen = index - start, postLen = end - index - 1;
        StringBuilder msg = new StringBuilder((amount + 5) * 2 + msgLen + 30);
        if (msgLen > 0) {
            msg.append("\n Message : ").append(message);
        }
        msg.append("\n Context : ").append(">>>>>").append(chars, start, maxLen).append("<<<<<");
        msg.append("\n Location: >>>>>")
            .append(chars, start, preLen)
            .append('ˇ')
            .append(chars[index])
            .append('ˇ')
            .append(chars, index + 1, postLen)
            .append("<<<<<");
        throw new IllegalArgumentException(msg.toString());
    }

    /**
     * 将 Optional、Iterable、Collection、Map 等拆箱
     *
     * @param object
     *
     * @return
     */
    public static Object unboxing(Object object) {
        NullableFunction resolver = TypeofOptional.resolveByObject(object);
        if (resolver == null) {
            if (object instanceof Optional) {
                return ((Optional<?>) object).getOrNull();
            } else {
                return onlyItemOrSize(object);
            }
        } else {
            return resolver.nullable(object);
        }
    }

    /**
     * 当数组或集合只有一项时，返回第一项，否则返回数组长度或集合 size
     * 其他自定义对象或集合均抛出异常
     *
     * @param o 目标集合
     *
     * @return 第一项或集合长度
     */
    public static Object onlyItemOrSize(Object o) {
        if (o == null) { return null; }
        int size;
        if (o.getClass().isArray()) { return ((size = Array.getLength(o)) == 1) ? Array.get(o, 0) : size; }
        if (o instanceof Collection) {
            return (size = ((Collection) o).size()) == 1 ? ((Collection) o).iterator().next() : size;
        }
        if (o instanceof Map) {
            return (size = ((Map) o).size()) == 1 ? ((Map) o).values().iterator().next() : size;
        }
        if (o instanceof Iterable) { return ((Iterable) o).iterator().next(); }
        throw new IllegalArgumentException();
    }

    public static <T> T matchOne(Collection<T> collection, Predicate<? super T> test) {
        if (collection != null) { for (T t : collection) { if (test.test(t)) { return t; } } }
        return null;
    }

    public static <T> T requireOne(Collection<T> collection, Predicate<? super T> test) {
        if (collection != null) { for (T t : collection) { if (test.test(t)) { return t; } } }
        throw new NullPointerException();
    }

    public static <T> T requireOne(Collection<T> collection, Predicate<? super T> test, String message) {
        if (collection != null) { for (T t : collection) { if (test.test(t)) { return t; } } }
        throw new IllegalArgumentException(message);
    }

    public static char[] setChar(char[] chars, int index, char ch) {
        chars = defaultChars(chars);
        int len = chars.length;
        if (index >= len) {
            char[] newArr = new char[Math.max(len << 1, 8)];
            System.arraycopy(chars, 0, newArr, 0, len);
            chars = newArr;
        }
        chars[index] = ch;
        return chars;
    }

    public static <T> T[] setArrItem(T[] arr, int index, T item) {
        int len = arr.length;
        if (index >= len) {
            Class type = arr.getClass().getComponentType();
            T[] newArray = (T[]) Array.newInstance(type, len << 1);
            System.arraycopy(arr, 0, newArray, 0, len);
            arr = newArray;
        }
        arr[index] = item;
        return arr;
    }

    public static char[] safeGetChars(char[] chars, int index, String str) {
        chars = defaultChars(chars);
        int strLen = str.length();
        int charsL = chars.length;
        int endIdx = strLen + index;
        if (strLen + index > charsL) {
            char[] newArr = new char[endIdx + 8];
            System.arraycopy(chars, 0, newArr, 0, charsL);
            chars = newArr;
        }
        str.getChars(0, strLen, chars, index);
        return chars;
    }

    public static char[] ensureToLength(char[] chars, int newLength, int presentLength) {
        if (chars == null) { return new char[newLength]; }
        if (chars.length < newLength) {
            char[] newArr = new char[newLength];
            System.arraycopy(chars, 0, newArr, 0, presentLength);
            return newArr;
        }
        return chars;
    }

    public static char[] defaultChars(char[] chars) { return defaultChars(chars, 8); }

    public static char[] defaultChars(char[] chars, int length) {
        return chars == null ? new char[length] : chars;
    }
}
