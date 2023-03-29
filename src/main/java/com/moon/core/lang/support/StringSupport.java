package com.moon.core.lang.support;

import com.moon.core.enums.Arrays2;
import com.moon.core.lang.CharUtil;
import com.moon.core.util.function.BiIntFunction;
import com.moon.core.util.function.IntBiFunction;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 所有以{@code Support}结尾的类不建议使用
 *
 * @author moonsky
 */
public final class StringSupport {

    static final char[] MARK = {'{', '}'};
    static final char[] EMPTY = Arrays2.CHARS.empty();

    public final static void checkIndexesBetween(int from, int to, int len) {
        if (from < 0) {
            throw new ArrayIndexOutOfBoundsException(from);
        }
        if (to >= len) {
            throw new ArrayIndexOutOfBoundsException(to);
        }
    }

    public static boolean matches(CharSequence str1, CharSequence str2) {
        int len = str1.length();
        final Class type;
        if (len == str2.length() && ((type = str1.getClass()) == str2.getClass())) {
            if (type == StringBuffer.class) {
                return str1.toString().equals(str2.toString());
            }
            for (int i = 0; i < len; i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // public static boolean regionMatchs

    public static boolean regionMatches(
        CharSequence cs, boolean ignoreCase, int thisStart, CharSequence substring, int start, int length
    ) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int len = length;

            while (len-- > 0) {
                char c1 = cs.charAt(index1++);
                char c2 = substring.charAt(index2++);
                if (c1 != c2) {
                    if (!ignoreCase) {
                        return false;
                    }

                    if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character
                        .toLowerCase(c2)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public static String concatHandler(Predicate<CharSequence> predicate, CharSequence... css) {
        int length = css.length;
        if (length > 0) {
            StringBuilder sb = new StringBuilder(length * 16);
            CharSequence tmp;
            for (int i = 0, len = length; i < len; i++) {
                tmp = css[i];
                if (predicate.test(tmp)) {
                    sb.append(tmp);
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static String concatHandler(BiIntFunction<CharSequence, CharSequence> formatter, CharSequence... css) {
        int length = css.length;
        if (length > 0) {
            StringBuilder sb = new StringBuilder(length * 16);
            for (int i = 0, len = length; i < len; i++) {
                sb.append(formatter.apply(css[i], i));
            }
            return sb.toString();
        }
        return "";
    }

    public static char[] getChars(String str, int start, int end, char[] chars, int begin) {
        final int len1 = end - start, len2 = chars == null ? 0 : chars.length, total = begin + len1;
        if (total > len2) {
            char[] newArr = new char[total << 1];
            if (chars != null) {
                System.arraycopy(chars, 0, newArr, 0, Math.min(begin, len2));
            }
            chars = newArr;
        }
        if (len1 > 0) {
            str.getChars(start, end, chars, begin);
        }
        return chars;
    }

    public static char[] setString(char[] chars, int begin, String str) {
        return getChars(str, 0, str.length(), chars, begin);
    }

    public static <R> R formatBuilder(IntBiFunction<char[], R> function, char[] template, Object... values) {
        return formatBuilder(function, false, template, values);
    }

    public static <R> R formatBuilder(
        IntBiFunction<char[], R> function, String placeholder, char[] template, Object... values
    ) { return formatBuilder(function, placeholder.toCharArray(), false, template, values); }

    public static <R> R formatBuilder(
        IntBiFunction<char[], R> function, boolean appendIfOverflow, char[] template, Object... values
    ) { return formatBuilder(function, MARK, appendIfOverflow, template, values); }

    public static <R> R formatBuilder(
        IntBiFunction<char[], R> function,
        final char[] marks,
        boolean appendIfOverflow,
        char[] template,
        Object... values
    ) {
        template = template == null ? EMPTY : template;

        final int valuesLen = values.length;
        final int tempLen = template.length;

        char[] chars = EMPTY;

        int valueIndex = 0;
        int charIndex = 0;
        int currIndex = 0;
        int lastIndex = 0;

        String temp;

        do {
            currIndex = CharUtil.indexOf(template, marks, currIndex);
            if (currIndex < 0) {
                if (appendIfOverflow) {
                    currIndex = tempLen;
                    chars = copyOfRangeTo(template, lastIndex, tempLen, chars, charIndex, chars.length, true);
                    charIndex = currIndex - lastIndex + charIndex;

                    for (; valueIndex < valuesLen; valueIndex++) {
                        temp = String.valueOf(values[valueIndex]);
                        chars = setString(chars, charIndex, temp);
                        charIndex += temp.length();
                    }
                }
                return function.apply(charIndex, chars);
            } else {
                chars = copyOfRangeTo(template, lastIndex, tempLen, chars, charIndex, chars.length, true);
                charIndex = currIndex - lastIndex + charIndex;

                temp = String.valueOf(values[valueIndex++]);
                chars = setString(chars, charIndex, temp);
                charIndex += temp.length();

                currIndex += 2;
                lastIndex = currIndex;
            }
        } while (valueIndex < valuesLen);
        chars = copyOfRangeTo(template, lastIndex, tempLen, chars, charIndex, chars.length, true);
        return function.apply(tempLen - lastIndex + charIndex, chars);
    }

    private static char[] copyOfRangeTo(
        Object chars, int fromIndex, int toIndex, char[] to, int distPos, int toLen, boolean newIfNeed
    ) {

        if (newIfNeed) {
            to = to == null ? new char[0] : to;
        }

        final int l1 = toIndex - fromIndex, l3 = l1 + distPos;
        if (l3 > toLen) {
            if (newIfNeed) {
                char[] now = new char[l3];
                System.arraycopy(to, 0, now, 0, distPos);
                to = now;
            } else {
                String msg = "new length: " + l3 + ", current length: " + toLen;
                throw new ArrayIndexOutOfBoundsException(msg);
            }
        }
        System.arraycopy(chars, fromIndex, to, distPos, l1);
        return to;
    }

    public static <T> T formatBuilder(IntBiFunction<char[], T> function, String template, Object... values) {
        return formatBuilder(function, template == null ? EMPTY : template.toCharArray(), values);
    }
}
