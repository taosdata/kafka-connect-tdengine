package com.moon.core.net;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author moonsky
 */
public final class URLUtil {

    private URLUtil() { ThrowUtil.noInstanceError(); }

    public static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String forwardSlash(CharSequence url) {
        String stringify = StringUtil.stringify(url);
        return stringify == null ? null : stringify.replace('\\', '/');
    }

    /**
     * 拼接 url 片段，保持 url 片段之间有且只有一个路径分隔符“/"
     *
     * @param first        第一个片段
     * @param second       第二个片段
     * @param urlFragments 其他片段
     *
     * @return 拼接完成的 url，可能为 null
     */
    public static String concatUrls(CharSequence first, CharSequence second, CharSequence... urlFragments) {
        String resultUrl = concatUrls(first, second);
        if (urlFragments != null) {
            for (CharSequence fragment : urlFragments) {
                resultUrl = concatUrls(resultUrl, fragment);
            }
        }
        return resultUrl;
    }

    /**
     * 拼接 url 片段，保持 url 片段之间有且只有一个路径分隔符“/"
     *
     * @param front  第一个片段
     * @param behind 第二个片段
     *
     * @return 拼接完成的 url，可能为 null
     */
    public static String concatUrls(CharSequence front, CharSequence behind) {
        if (StringUtil.isEmpty(front)) {
            return StringUtil.stringify(behind);
        }
        if (StringUtil.isEmpty(behind)) {
            return StringUtil.stringify(front);
        }
        char frontLast = StringUtil.charAt(front, -1);
        char behindFirst = behind.charAt(0);
        char d1 = '/', d2 = '\\';
        if (frontLast == d1 || frontLast == d2) {
            if (behindFirst == d1 || behindFirst == d2) {
                return front + behind.toString().substring(1);
            } else {
                return front.toString() + behind;
            }
        } else {
            if (behindFirst == d1 || behindFirst == d2) {
                return front.toString() + behind;
            } else {
                return StringUtil.concat(front, "/", behind);
            }
        }
    }
}
