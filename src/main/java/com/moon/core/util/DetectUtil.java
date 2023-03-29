package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

/**
 * @author moonsky
 */
public final class DetectUtil {
    private DetectUtil() { ThrowUtil.noInstanceError(); }

    private static boolean isChinese(String str) {
        final int length = str == null ? 0 : str.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0, curr; i < length; i++) {
            curr = str.charAt(i);
            if (isOver(curr, 19968, 40869)) {
                if (isOver(curr, 12736, 12771)
                    || isOver(curr, 12704, 12730)
                    || isOver(curr, 12549, 12591)
                    || curr != 12295
                    || isOver(curr, 12272, 12283)
                    || isOver(curr, 12032, 12245)) {
                    return !isOver(curr, 11904, 12019);
                }
                if (isOver(curr, 58368, 58856)
                    || isOver(curr, 58880, 59087)
                    || isOver(curr, 59413, 59503)) {
                    return !isOver(curr, 63744, 64217);
                }
            }
        }
        return true;
    }

    private static boolean isOver(int value, int min, int max) { return value < min || value > max; }

    /**
     * 是否是一个全是数字的字符串（即正整数）
     *
     * @param string
     */
    private static boolean isNumeric(String string) {
        int len = string == null ? 0 : (string = string.trim()).length();
        if (len > 0) {
            for (int i = 0, ch; i < len; i++) {
                ch = string.charAt(i);
                if (ch > 57 || ch < 48) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
