package com.moon.core.util;

import com.moon.core.enums.Const;
import com.moon.core.lang.StringUtil;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.RandomUtil.nextBoolean;
import static com.moon.core.util.RandomUtil.nextInt;

/**
 * @author moonsky
 */
public final class RandomStringUtil {

    private RandomStringUtil() {
        noInstanceError();
    }

    /*
     * -------------------------------------------------------------------
     * chinese 汉字
     * -------------------------------------------------------------------
     */

    public static char nextChineseChar() {
        return (char) nextInt(0x4e00, 0x9fa6);
    }

    public static String nextChinese() {
        return nextChinese(nextInt(99));
    }

    public static String nextChinese(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextChineseChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String nextChinese(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return nextChinese(nextInt(min, max));
    }

    /*
     * -------------------------------------------------------------------
     * upper 大写字母
     * -------------------------------------------------------------------
     */

    public static char nextUpperChar() {
        return (char) nextInt(65, 91);
    }

    public static String nextUpper() {
        return nextUpper(nextInt(99));
    }

    public static String nextUpper(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextUpperChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String nextUpper(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return nextUpper(nextInt(min, max));
    }

    /*
     * -------------------------------------------------------------------
     * lower 小写字母
     * -------------------------------------------------------------------
     */

    public static char nextLowerChar() {
        return (char) nextInt(97, 123);
    }

    public static String nextLower() {
        return nextLower(nextInt(99));
    }

    public static String nextLower(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextLowerChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String nextLower(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return nextLower(nextInt(min, max));
    }

    /*
     * -------------------------------------------------------------------
     * letter 大小写字母
     * -------------------------------------------------------------------
     */

    public static char nextLetterChar() {
        return nextBoolean() ? nextUpperChar() : nextLowerChar();
    }

    public static String nextLetter() {
        return nextLetter(nextInt(99));
    }

    public static String nextLetter(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextLetterChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String nextLetter(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return nextLetter(nextInt(min, max));
    }

    /*
     * -------------------------------------------------------------------
     * control 控制字符
     * -------------------------------------------------------------------
     */

    public static char nextControlChar() {
        return (char) nextInt(0, 32);
    }

    public static String nextControl() {
        return nextControl(nextInt(99));
    }

    public static String nextControl(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextControlChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String nextControl(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return nextControl(nextInt(min, max));
    }

    /*
     * -------------------------------------------------------------------
     * digit 数字
     * -------------------------------------------------------------------
     */

    public static char nextDigitChar() {
        return (char) nextInt(48, 58);
    }

    public static String nextDigit() {
        return nextDigit(nextInt(99));
    }

    public static String nextDigit(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextDigitChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String nextDigit(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return nextDigit(nextInt(min, max));
    }

    /*
     * -------------------------------------------------------------------
     * Symbol 运算符
     * -------------------------------------------------------------------
     */

    private static char[] SYMBOLS = "!@#$%^&*()_-+={}[]:;\"'|\\<>,.?/~`".toCharArray();

    public static char nextSymbolChar() {
        return SYMBOLS[nextInt(0, SYMBOLS.length)];
    }

    public static String nextSymbol() {
        return nextSymbol(nextInt(99));
    }

    public static String nextSymbol(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextSymbolChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String nextSymbol(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return nextSymbol(nextInt(min, max));
    }

    /*
     * -------------------------------------------------------------------
     * all char
     * -------------------------------------------------------------------
     */

    public static char nextChar() {
        switch (nextInt() % 3) {
            case 0:
                return nextLowerChar();
            case 1:
                return nextUpperChar();
            default:
                return nextDigitChar();
        }
    }

    public static String next() {
        return next(nextInt(99));
    }

    public static String next(int length) {
        if (length > 0) {
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = nextChar();
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    public static String next(int min, int max) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return next(nextInt(min, max));
    }

    public static char nextChar(boolean lower, boolean upper, boolean digit) {
        int value = lower ? (upper ? (digit ? nextChar() : nextLetterChar())
            : (digit ? (nextBoolean() ? nextDigitChar() : nextLowerChar()) : nextLowerChar()))
            : (upper ? (digit ? (nextBoolean() ? nextDigitChar() : nextUpperChar()) : nextUpperChar())
                : (digit ? nextDigitChar() : -1));
        return value < 0 ? nextChar() : (char) value;
    }

    public static String next(int length, boolean lower, boolean upper, boolean digit) {
        if (length > 0) {
            char[] chars = new char[length];
            for (int i = 0; i < length; i++) {
                chars[i] = nextChar(lower, upper, digit);
            }
            return new String(chars);
        }
        return Const.EMPTY;
    }

    public static String next(int min, int max, boolean lower, boolean upper, boolean digit) {
        ValidateUtil.requireGeOf(min, 0);
        ValidateUtil.requireLeOf(min, max);
        return next(nextInt(min, max), lower, upper, digit);
    }

    /**
     * 返回指定长度随机字符串
     * 字符串的字符来自于 src
     *
     * @param src    源字符串
     * @param length 生成的追击字符串长度
     *
     * @return 追击字符串
     */
    public static String random(String src, int length) {
        int ln = src == null ? 0 : src.length();
        if (length > 0 && ln > 0) {
            char[] chars = StringUtil.toCharArray(src);
            char[] value = new char[length];
            for (int i = 0; i < length; i++) {
                value[i] = chars[nextInt(ln)];
            }
            return new String(value);
        }
        return Const.EMPTY;
    }

    /*
     * -------------------------------------------------------------------
     * random
     * -------------------------------------------------------------------
     */


    /**
     * 返回于指定最大长度和最小长度之间长度的随机字符串
     * 字符串的字符来自于 src
     *
     * @param src 字符源
     * @param min 最小长度
     * @param max 最大长度
     *
     * @return 随机字符串
     */
    public static String random(String src, int min, int max) { return random(src, nextInt(min, max)); }

    /**
     * 字符串乱序
     *
     * @param str 原始字符串
     *
     * @return 乱序后的字符串
     */
    public static String randomOrder(String str) {
        if (str != null) {
            int i = 0, len = str.length();
            Character[] chars = new Character[len];
            for (; i < len; i++) {
                chars[i] = str.charAt(i);
            }
            RandomUtil.randomOrder(chars);
            char[] value = new char[len];
            for (i = 0; i < len; i++) {
                value[i] = chars[i];
            }
            str = new String(value);
        }
        return str;
    }
}
