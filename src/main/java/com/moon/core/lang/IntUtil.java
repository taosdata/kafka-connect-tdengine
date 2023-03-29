package com.moon.core.lang;

import com.moon.core.util.CollectUtil;
import com.moon.core.util.function.BiIntFunction;
import com.moon.core.util.function.TableIntFunction;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.ToIntFunction;

import static com.moon.core.enums.Strings.*;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.TestUtil.isIntegerValue;
import static java.lang.String.format;

/**
 * @author moonsky
 */
public final class IntUtil {

    private IntUtil() { noInstanceError(); }


    public static boolean isInt(Object o) { return o instanceof Integer; }

    public static boolean matchInt(CharSequence o) { return isIntegerValue(String.valueOf(o)); }

    /*
     * -------------------------------------------------------------------------------------------
     * converter
     * -------------------------------------------------------------------------------------------
     */

    public static int toIntValue(Boolean bool) { return toIntValue(bool != null && bool); }

    public static int toIntValue(long value) { return (int) value; }

    public static int toIntValue(float value) { return (int) value; }

    public static int toIntValue(double value) { return (int) value; }

    public static int toIntValue(boolean value) { return value ? 1 : 0; }

    public static int toIntValue(CharSequence cs) {
        if (cs == null) {
            return 0;
        } else {
            String str = cs.toString().trim();
            return str.length() > 0 ? Integer.parseInt(str) : 0;
        }
    }

    /*
     * -------------------------------------------------------------------------------------------
     * defaulter
     * -------------------------------------------------------------------------------------------
     */

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return defaultVal
     *
     * @param cs         数字字符串
     * @param defaultVal 默认值
     *
     * @return 数值
     */
    public static int defaultIfInvalid(CharSequence cs, int defaultVal) {
        try {
            if (cs == null) {
                return defaultVal;
            }
            String value = cs.toString().trim().toLowerCase();
            return StringUtil.isEmpty(value) ? defaultVal : Integer.parseInt(value);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return 0
     *
     * @param cs 数字字符串
     *
     * @return 数字
     */
    public static int zeroIfInvalid(CharSequence cs) {
        return defaultIfInvalid(cs, 0);
    }

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return 1
     *
     * @param cs 数字字符串
     *
     * @return 数字
     */
    public static int oneIfInvalid(CharSequence cs) {
        return defaultIfInvalid(cs, 1);
    }

    /*
     * -------------------------------------------------------------------------------------------
     * calculator
     * -------------------------------------------------------------------------------------------
     */

    public static int[] toInts(int... values) { return values; }

    public static int[] toInts(String... values) {
        return toInts(Integer::parseInt, values);
    }

    @SafeVarargs
    public static <T> int[] toInts(ToIntFunction<? super T> transformer, T... values) {
        if (values == null) {
            return null;
        }
        final int length = values.length;
        int[] ints = new int[length];
        for (int i = 0; i < length; i++) {
            ints[i] = transformer.applyAsInt(values[i]);
        }
        return ints;
    }

    public static <T> int[] toInts(Collection<T> collect, ToIntFunction<? super T> transformer) {
        if (collect == null) {
            return null;
        }
        int index = 0;
        int[] ints = new int[collect.size()];
        for (T item : collect) {
            ints[index++] = transformer.applyAsInt(item);
        }
        return ints;
    }

    public static int max(int... values) {
        int len = values.length;
        int ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static int min(int... values) {
        int len = values.length;
        int ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static int avg(int... values) {
        int ret = 0;
        int len = values.length;
        for (int i = 0; i < len; ret += values[i++]) {
        }
        return ret / len;
    }

    public static int sum(int... values) {
        int ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static int multiply(int... values) {
        int ret = 1;
        int le = values.length;
        for (int i = 0; i < le; i++) {
            ret *= values[i];
        }
        return ret;
    }

    /**
     * 目前基本数据 Util 内类似的方法均使用了<strong>极大的容忍度</strong>
     * * 对于普通的转换均能得到预期结果；
     * 对于复杂对象（数组或集合，但不包括自定义对象）的转换需要熟悉方法内部逻辑；
     * * 如果对象 o 是一个集合或数组，当 o 只有一项时，返回这一项并且深度递归
     * * 否则返回这个集合或数组的尺寸（size 或 length）
     * <p>
     * boolean value = true;  // =============================== 1
     * boolean value = false;  // ============================== 0
     * char value = 'a';  // =================================== 97
     * byte value = 1;  // ===================================== 1
     * int value = 1;  // ====================================== 1
     * short value = 1;  // ==================================== 1
     * long value = 1L;  // ==================================== 1
     * float value = 1F;  // =================================== 1
     * double value = 1F;  // ================================== 1
     * String value = "1";  // ================================= 1
     * StringBuffer value = new StringBuffer("1");  // ========= 1
     * StringBuilder value = new StringBuilder("1");  // ======= 1
     * String value = "  1   ";  // ============================ 1
     * StringBuffer value = new StringBuffer("  1   ");  // ==== 1
     * StringBuilder value = new StringBuilder("  1   ");  // == 1
     * BigDecimal value = new BigDecimal("1");  // ============= 1
     * BigInteger value = new BigInteger("1");  // ============= 1
     * Collection value = new ArrayList(){{put(1)}};  // ======= 1（只有一项时）
     * Collection value = new HashSet(){{put(1)}};  // ========= 1（只有一项时）
     * Collection value = new TreeSet(){{put(1)}};  // ========= 1（只有一项时）
     * Collection value = new LinkedList(){{put(1)}};  // ====== 1（只有一项时）
     * Map value = new HashMap(){{put("key", 1)}};  // ========= 1（只有一项时）
     * <p>
     * int[] value = {1, 2, 3, 4};  // ======================================== 4（大于一项时，返回 size）
     * String[] value = {"1", "1", "1", "1"};  // ============================= 4（大于一项时，返回 size）
     * Collection value = new ArrayList(){{put(1);put(1);put(1);}};  // ======= 3（大于一项时，返回 size）
     * Map value = new HashMap(){{put("key", 1);put("name", 2);}};  // ======== 2（大于一项时，返回 size）
     * <p>
     * int result = IntUtil.toIntValue(value);
     *
     * @param o 待转换值
     *
     * @return 转换后的值
     *
     * @see BooleanUtil#toBoolean(Object)
     * @see BooleanUtil#toBooleanValue(Object)
     * @see CharUtil#toCharValue(Object)
     * @see CharacterUtil#toCharacter(Object)
     * @see ByteUtil#toByteValue(Object)
     * @see ByteUtil#toByte(Object)
     * @see ShortUtil#toShort(Object)
     * @see ShortUtil#toShortValue(Object)
     * @see IntegerUtil#toInteger(Object)
     * @see LongUtil#toLongValue(Object)
     * @see LongUtil#toLong(Object)
     * @see FloatUtil#toFloat(Object)
     * @see FloatUtil#toFloatValue(Object)
     * @see DoubleUtil#toDoubleValue(Object)
     * @see DoubleUtil#toDouble(Object)
     */
    public static int toIntValue(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        if (o instanceof CharSequence) {
            return toIntValue((CharSequence) o);
        }
        if (o instanceof Character) {
            return (Character) o;
        }
        if (o instanceof Boolean) {
            return (boolean) o ? 1 : 0;
        }
        try {
            return toIntValue(ParseSupportUtil.unboxing(o));
        } catch (Exception e) {
            throw new IllegalArgumentException(format("Can not cast to int of: %s", o), e);
        }
    }

    /**
     * 聚合函数，参照 JavaScript 中 Array.reduce(..) 实现
     * <pre>
     * 1. 接受一个数组作为源数据；
     * 2. 一个处理器，处理器接收两个参数（总值, 当前项），然后返回总值，下一次迭代接受到的总值是上一次的返回结果；
     *       其中当前项也是索引，索引相对{@link #reduce(int, BiIntFunction, Object)}少一个参数
     * 3. 初始值，作为第一次传入处理器的参数
     * </pre>
     *
     * @param count      迭代次数
     * @param reducer    聚合函数
     * @param totalValue 返回结果
     * @param <T>        返回值类型
     *
     * @return 返回最后一项处理完后的结果
     *
     * @see ArrayUtil#reduce(Object[], TableIntFunction, Object)
     * @see CollectUtil#reduce(Iterable, TableIntFunction, Object)
     * @see CollectUtil#reduce(Iterator, TableIntFunction, Object)
     */
    public static <T> T reduce(int count, BiIntFunction<? super T, T> reducer, T totalValue) {
        for (int i = 0; i < count; i++) {
            totalValue = reducer.apply(totalValue, i);
        }
        return totalValue;
    }

    final static char[] DIGITS = toCharArray(NUMBERS, UPPERS, LOWERS);

    final static int TEN = 10;

    /**
     * 进制转换：支持十进制至 2 ~ 62 进制的转换
     * （Copied from jdk 1.8: {@link Integer#toString(int, int)}）
     * <p>
     * {@code Integer}仅支持 36 进制转换，这里扩展到 62 进制
     * 与{@link Integer#parseInt(String)}、{@link Integer#toString(int, int)}不兼容
     *
     * @param value 待转换的十进制整型数
     * @param radix 进制
     *
     * @return 转换后的字符串
     *
     * @see LongUtil#toString(long, int) 长整形进制转换
     */
    public static String toString(int value, int radix) {
        if (radix < Character.MIN_RADIX) {
            radix = TEN;
        }
        if (radix > DIGITS.length) {
            radix = DIGITS.length;
        }
        if (radix == TEN) {
            return Integer.toString(value);
        }
        int maxLen = radix < TEN ? 33 : 11;

        char[] buf = new char[maxLen];
        boolean negative = (value < 0);
        int charPos = maxLen - 1;

        if (!negative) {
            value = -value;
        }

        while (value <= -radix) {
            buf[charPos--] = DIGITS[-(value % radix)];
            value = value / radix;
        }
        buf[charPos] = DIGITS[-value];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (maxLen - charPos));
    }

    /**
     * 最大 62 进制字符串解析成十进制整数，和上面的{@link #toString(int, int)}相对应
     * 与{@link Integer#parseInt(String, int)}、{@link Integer#toString(int, int)}不兼容
     *
     * @param s     待解析字符串
     * @param radix 进制
     *
     * @return 解析后的整数
     *
     * @throws NumberFormatException 进制错误时抛出异常
     */
    @SuppressWarnings("all")
    public static int parseInt(String s, int radix) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        if (radix < 2) {
            throw new NumberFormatException("radix " + radix + " less than 2");
        }
        if (radix > DIGITS.length) {
            throw new NumberFormatException("radix " + radix + " greater than 62");
        }
        int result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;
        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw new NumberFormatException(s);
                }
                if (len == 1) {
                    // Cannot have lone "+" or "-"
                    throw new NumberFormatException(s);
                }
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                digit = CharUtil.toDigitMaxAs62(s.charAt(i++));
                if (digit < 0) {
                    throw new NumberFormatException(s);
                }
                if (result < multmin) {
                    throw new NumberFormatException(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException(s);
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException(s);
        }
        return negative ? result : -result;
    }

    final static char MIN_CHINESE_CHAR = '\u4e00';
    final static char MAX_CHINESE_CHAR = '\u9fa5';
    public final static int MAX_RADIX = MAX_CHINESE_CHAR - MIN_CHINESE_CHAR + 1;

    /**
     * 高压缩进制转换（最高支持 20902 进制, {@link #MAX_CHINESE_CHAR}-{@link #MIN_CHINESE_CHAR}+1）
     * <p>
     * 这个区间是正则表达式汉字所在的区间，也是电脑能显示的一段较大的连续字符区间；
     * <p>
     * 还原原数字：{@link #parseCompressionString(String, int)}
     * <p>
     * 兼容{@link #toString(int, int)}；
     * 兼容{@link #parseInt(String, int)}；
     * 不兼容{@link Integer#toString(int, int)}；
     *
     * @param value 值
     * @param radix 进制
     *
     * @return 转换后的字符串
     */
    public static String toCompressionString(int value, int radix) {
        if (radix < 63) {
            return toString(value, radix);
        }
        final int min = MIN_CHINESE_CHAR, maxLen = 5;
        radix = Math.min(MAX_RADIX, radix);

        char[] buf = new char[maxLen];
        boolean negative = (value < 0);
        int charPos = maxLen - 1;
        if (!negative) {
            value = -value;
        }
        while (value <= -radix) {
            buf[charPos--] = (char) (min - (value % radix));
            value = value / radix;
        }
        buf[charPos] = (char) (min - value);
        if (negative) {
            buf[--charPos] = '-';
        }
        return new String(buf, charPos, (maxLen - charPos));
    }

    /**
     * 高压缩进制解析（最高支持 20902 进制)
     * <p>
     * 用于解析{@link #toCompressionString(int, int)}生成的字符串
     * <p>
     * 兼容{@link #toString(int, int)}；
     * 不兼容{@link Integer#toString(int, int)}；
     *
     * @param s     字符串
     * @param radix 进制
     *
     * @return 转换后的数字
     */
    @SuppressWarnings("all")
    public static int parseCompressionString(String s, int radix) {
        if (radix < 63) {
            return parseInt(s, radix);
        }
        if (s == null) {
            throw new NumberFormatException("null");
        }
        if (radix > MAX_RADIX) {
            throw new NumberFormatException("radix " + radix + " greater than " + MAX_RADIX);
        }
        int result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;
        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') {
                // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw new NumberFormatException(s);
                }
                if (len == 1) {
                    // Cannot have lone "+" or "-"
                    throw new NumberFormatException(s);
                }
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                digit = s.charAt(i++) - MIN_CHINESE_CHAR;
                if (digit < 0) {
                    throw new NumberFormatException(s);
                }
                if (result < multmin) {
                    throw new NumberFormatException(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException(s);
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException(s);
        }
        return negative ? result : -result;
    }
}
