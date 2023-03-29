package com.moon.core.lang;

import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class CharUtil {

    private CharUtil() { noInstanceError(); }

    /**
     * 字符片段是否相等
     *
     * @param src       字符数组 1
     * @param srcStart  起始位置
     * @param srcEnd    结束位置
     * @param dest      字符数组 2
     * @param destStart 起始位置
     * @param destEnd   结束位置
     *
     * @return 如果两个字符数组指定片段内的内容相同返回 true，否则返回 false
     */
    public static boolean isRegionMatches(
        char[] src, int srcStart, int srcEnd, char[] dest, int destStart, int destEnd
    ) {
        int srcCnt = srcEnd - srcStart, destCnt = destEnd - destStart;
        if (srcCnt != destCnt || srcStart < 0 || destStart < 0) {
            return false;
        }
        int srcLen = src.length;
        if (srcEnd >= srcLen) {
            return false;
        }
        int destLen = dest.length;
        if (destEnd >= destLen) {
            return false;
        }
        for (int i = 0; i < srcCnt; i++) {
            if (src[srcStart + i] != dest[destStart + i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符片段是否相等
     * <p>
     * 这个方法默认由调用方保证索引位置安全
     *
     * @param src       字符数组 1
     * @param srcStart  起始位置
     * @param dest      字符数组 2
     * @param destStart 起始位置
     *
     * @return 如果两个字符数组指定片段内的内容相同返回 true，否则返回 false
     */
    public static boolean isSafeRegionMatches(char[] src, int srcStart, char[] dest, int destStart) {
        int count = dest.length - destStart;
        if (srcStart + count <= src.length) {
            for (int i = 0; i < count; i++) {
                if (src[srcStart + i] != dest[destStart + i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static int indexOf(final char[] src, final char[] test) { return indexOf(src, test, 0); }

    public static int indexOf(final char[] src, final char[] test, int fromIndex) {
        BooleanUtil.requireFalse(fromIndex < 0);

        if (src == test) {
            return fromIndex > 0 ? -1 : 0;
        }

        final int l1 = src.length, l2 = test.length;
        if (fromIndex == l1) {
            return -1;
        }
        BooleanUtil.requireTrue(fromIndex < l1);

        if (l2 == 0) {
            return fromIndex;
        }

        if (l2 > l1) {
            return -1;
        }

        final char first = test[0];
        for (int i = fromIndex, idx, actIdx; i < l1; i++) {
            if (src[i] == first) {
                idx = 1;
                for (; idx < l2; idx++) {
                    actIdx = idx + i;
                    if (actIdx < l1) {
                        if (src[actIdx] != test[idx]) {
                            break;
                        }
                    } else {
                        return -1;
                    }
                }
                if (idx >= l2) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static boolean isVarStarting(int ch) { return isLetter(ch) || is_(ch) || is$(ch) || isChinese(ch); }

    public static boolean isVar(int ch) { return isLetterOrDigit(ch) || is_(ch) || is$(ch) || isChinese(ch); }

    public static boolean is_(int ch) { return isUnderscore(ch); }

    public static boolean is$(int ch) { return ch == '$'; }

    public static boolean isUnderscore(int ch) { return ch == '_'; }

    public static boolean isChineseYuan(int ch) { return ch == '￥'; }

    public static boolean isDollar(int ch) { return is$(ch); }

    /**
     * "[4e00-9fa5]"
     *
     * @param ch 字符
     *
     * @return 是否是汉字
     */
    public static boolean isChinese(int ch) { return ch < 40870 && ch > 19967; }

    public static boolean isLetterOrDigit(int ch) { return isDigit(ch) || isLetter(ch); }

    /**
     * A-Z,a-z
     *
     * @param ch 字符
     *
     * @return 是否是字母
     */
    public static boolean isLetter(int ch) { return isLowerCase(ch) || isUpperCase(ch); }

    /**
     * A-Z
     *
     * @param ch 字符
     *
     * @return 是否是大写字母
     */
    public static boolean isUpperCase(int ch) { return ch > 64 && ch < 91; }

    /**
     * a-z
     *
     * @param ch 字符
     *
     * @return 是否是小写字母
     */
    public static boolean isLowerCase(int ch) { return ch > 96 && ch < 123; }

    /**
     * 0-9
     *
     * @param ch 字符
     *
     * @return 是否是数字
     */
    public static boolean isDigit(int ch) { return ch > 47 && ch < 58; }

    public static boolean equalsIgnoreCase(int ch1, int ch2) {
        return ch1 == ch2 || (isLowerCase(ch1) ? ch1 - 32 == ch2 : (isUpperCase(ch1) && ch2 - 32 == ch1));
    }

    public static boolean isASCIICode(int ch) { return ch < 128; }

    public static boolean isChar(Object o) {
        if (o instanceof Character) {
            return true;
        }
        if (o instanceof Integer) {
            int value = ((Number) o).intValue();
            return value > -1 && value < 65536;
        }
        return false;
    }

    /**
     * 字符转换为数字
     * <pre>
     * 0-9:  0 -  9;
     * A-Z: 10 - 36;
     * a-z: 37 - 61;
     * (other): -1
     * </pre>
     *
     * @param codePoint
     *
     * @return
     */
    public static int toDigitMaxAs62(int codePoint) {
        if (isDigit(codePoint)) {
            return codePoint - 48;
        }
        if (isUpperCase(codePoint)) {
            return codePoint - 55;
        }
        if (isLowerCase(codePoint)) {
            return codePoint - 61;
        }
        return -1;
    }

    /**
     * null   : '0'
     * false  : '0'
     * (other): '1'
     *
     * @param bool
     *
     * @return
     */
    public static char toCharValue(Boolean bool) { return (char) (bool == null || !bool ? 48 : 49); }

    public static char toCharValue(long value) { return (char) value; }

    public static char toCharValue(float value) { return (char) value; }

    public static char toCharValue(double value) { return (char) value; }

    /**
     * 当且仅当字符串包含一个字符时，返回这个字符，否则抛出异常
     *
     * @param cs
     *
     * @return 字符串中仅有的一个字符
     *
     * @throws NullPointerException     当 cs == null 时，空指针异常
     * @throws IllegalArgumentException 当 cs 长度不符合要求 1 时抛出异常
     */
    public static char toCharValue(CharSequence cs) {
        Objects.requireNonNull(cs, "Can not cast to char from a null value.");
        if (cs.length() == 1) {
            return cs.charAt(0);
        }
        throw new IllegalArgumentException(String.format("Can not cast to char of: %s", cs));
    }

    /**
     * @param o 带转换值
     *
     * @return 转换后的值
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static char toCharValue(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Character) {
            return (Character) o;
        }
        if (o instanceof Number) {
            return (char) ((Number) o).intValue();
        }
        if (o instanceof CharSequence) {
            return toCharValue(o.toString());
        }
        if (o instanceof Boolean) {
            return toCharValue((Boolean) o);
        }
        try {
            return toCharValue(ParseSupportUtil.unboxing(o));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Can not cast to char of: %s", o), e);
        }
    }
}
