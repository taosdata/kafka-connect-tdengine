package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

/**
 * @author moonsky
 */
public final class UnicodeUtil {

    private UnicodeUtil() { ThrowUtil.noInstanceError(); }

    /**
     * 判断一个字符串是否是 Unicode 字符串
     *
     * @param s 待测字符串
     *
     * @return true: 待测字符串是 unicode 字符串
     */
    public static boolean isUnicode(String s) {
        return isFullUnicode(s) || isSimpleUnicode(s);
    }

    /**
     * 判断字符串是否不是 Unicode 字符串
     *
     * @param s
     */
    public static boolean isNotUnicode(String s) {
        return !isUnicode(s);
    }

    /**
     * 将一个 Unicode 字符串转换成普通字符串
     *
     * @param unicode
     */
    public static String toString(String unicode) {
        if (isFullUnicode(unicode)) {
            return toFullString(unicode);
        } else if (isSimpleUnicode(unicode)) {
            return toSimpleString(unicode);
        } else {
            return unicode;
        }
    }

    /**
     * 将一个普通字符串转换为 Unicode 字符串
     *
     * @param string
     */
    public static String toFullUnicode(String string) {
        if (string != null && isNotUnicode(string)) {
            String u = "\\u";
            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = string.length(); i < len; i++) {
                sb.append(u).append(Integer.toHexString(string.charAt(i)));
            }
            return sb.length() == 0 ? null : sb.toString();
        }
        return string;
    }

    /**
     * 将一个普通字符串转换为 Unicode 字符串
     *
     * @param string
     */
    public static String toSimpleUnicode(String string) {
        if (string != null && isNotUnicode(string)) {
            final String u = "\\u";
            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = string.length(); i < len; i++) {
                char ch = string.charAt(i);
                if (ch > 128) {
                    sb.append(u).append(Integer.toHexString(ch));
                } else {
                    sb.append(ch);
                }
            }
            return sb.length() == 0 ? null : sb.toString();
        }
        return string;
    }

    /**
     * 将 Unicode 字符串 采用方式  1 转换为普通字符串，不对外暴露
     *
     * @param source
     */
    private static String toFullString(String source) {
        if (source != null) {
            StringBuilder sb = new StringBuilder();
            String[] arr = source.toLowerCase().split("\\\\u");
            for (int i = 1, len = arr.length; i < len; i++) {
                sb.append((char) Integer.parseInt(arr[i], 16));
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 将 Unicode 字符串 采用方式  2 转换为普通字符串，不对外暴露
     *
     * @param source
     */
    private static String toSimpleString(String source) {
        if (source != null) {
            StringBuilder sb = new StringBuilder();
            String[] arr = source.toLowerCase().split("\\\\u");
            for (int i = 1, len = arr.length; i < len; i++) {
                String tmp = arr[i];
                if (tmp.length() > 4) {
                    int num = Integer.parseInt(tmp.substring(0, 4), 16);
                    sb.append((char) num).append(tmp.substring(4));
                } else {
                    sb.append((char) Integer.parseInt(tmp, 16));
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * 检测一个字符串是否是一个 Unicode 字符串
     * (纯 Unicode 所有字符都是 Unicode 表示)
     * 不对外暴露
     *
     * @param string
     */
    static boolean isFullUnicode(String string) {
        if (string != null) {
            int len = string.length();
            boolean isNumeric;
            if (len > 0) {
                for (int i = 0; i < len; ) {
                    char ch = string.charAt(i++);
                    if (ch == 92) {
                        ch = string.charAt(i++);
                        if (ch == 85 || ch == 117) {
                            do {
                                ch = string.charAt(i);
                                if (ch != 92) {
                                    if (!(isNumeric = Character.digit(ch, 16) > -1)) {
                                        return false;
                                    }
                                    i++;
                                } else {
                                    isNumeric = false;
                                }
                            } while (isNumeric && i < len);
                            continue;
                        }
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 检测一个字符串是否是一个 Unicode 字符串
     * ASCII 码是原样表示
     * 不对外暴露
     *
     * @param string
     */
    static boolean isSimpleUnicode(String string) {
        if (string != null) {
            String[] nodes = string.trim().split("\\\\u");
            int length = nodes.length;
            for (int i = nodes[0].length() == 0 ? 1 : 0; i < length; i++) {
                String tmp = nodes[i];
                int len = tmp.length();
                if (len >= 4) {
                    for (int j = 0; j < 4; j++) {
                        if ((Character.digit(tmp.charAt(j), 16) < 0)) {
                            return false;
                        }
                    }
                } else {
                    for (int j = 0; j < len; j++) {
                        if ((Character.digit(tmp.charAt(j), 16) < 0)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
