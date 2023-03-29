package com.moon.core.enums;

import com.moon.core.util.RequireValidateException;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.moon.core.enums.Const.CHAR_MINUS;
import static com.moon.core.lang.StringUtil.deleteChars;
import static java.lang.Character.isWhitespace;

/**
 * 正则表达式集合，用来检查字符串是否匹配规则
 *
 * @author moonsky
 * @see Testers 用来检查对象是否符合要求
 */
public enum Patterns implements Predicate<CharSequence> {
    /**
     * 数字
     */
    DIGIT(Pattern.compile("\\d+")),
    /**
     * 小写字母
     */
    LOWER(Pattern.compile("[a-z]+")),
    /**
     * 大写字母
     */
    UPPER(Pattern.compile("[A-Z]+")),
    /**
     * 字母
     */
    LETTER(Pattern.compile("[a-zA-Z]+")),
    /**
     * ASCII 编码(0 ~ 127)
     */
    ASCII(Pattern.compile("[\u0000-\007F]+")),
    /**
     * UNICODE 编码(0 ~ 65535)
     */
    UNICODE(Pattern.compile("[\u0000-\uFFFF]+")),
    /**
     * 18位，居民身份证号（这里允许了末尾大写和小写“X”，实际中正确的只有大写）
     */
    RESIDENT_ID_18(Pattern.compile("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)")),
    /**
     * 汉字
     */
    CHINESE_WORDS(Pattern.compile("[\u4E00-\u9FAF]+")),
    /**
     * 中国大陆手机号
     * <p>
     * 14xxxxxxxxx: 通常是网卡
     * 17xxxxxxxxx: 通常是虚拟运营商
     */
    CHINESE_MOBILE(Pattern.compile("(?:0|86|\\+86)?1[3456789]\\d{9}")) {
        @Override
        public boolean test(CharSequence str) {
            return super.test(deleteChars(str, ch -> ch == CHAR_MINUS || isWhitespace(ch)));
        }
    },
    /**
     * 中国邮政编码
     */
    CHINESE_ZIP_CODE(Pattern.compile("[1-9]\\d{5}(?!\\d)")),
    /**
     * 电子邮箱（是从哪找到的？忘了...）
     */
    EMAIL(Pattern.compile(
        "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$")),
    /**
     * MAC 地址
     */
    MAC_ADDRESS(Pattern.compile("((?:[A-F0-9]{1,2}[:-]){5}[A-F0-9]{1,2})|(?:0x)(\\d{12})(?:.+ETHER)")),
    /**
     * IP v4
     */
    IPV4(Pattern.compile("^(([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])$")),
    /**
     * IP v6
     * <p>
     * Ref: https://blog.csdn.net/jiangfeng08/article/details/7642018
     */
    IPV6(Pattern.compile(
        "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$")),
    /**
     * UUID（标准 UUID 格式）
     */
    UUID(Pattern.compile("^[\\da-z]{8}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{12}$")),
    /**
     * 简化 UUID（不带横线）
     */
    SIMPLIFY_UUID(Pattern.compile("^[\\da-z]{32}$")),
    /**
     * hibernate 框架采用的 UUID 格式
     */
    HIBERNATE_UUID(Pattern.compile("^[\\da-z]{8}-[\\da-z]{8}-[\\da-z]{4}-[\\da-z]{8}-[\\da-z]{4}$")),
    /**
     * GUID
     */
    GUID(Pattern.compile("^[\\da-z]{8}-[\\da-z]{4}-[\\da-z]{4}-[\\da-z]{16}$")),
    /**
     * 中国车牌号（兼容新能源）；字母无 I、O，容易和 0、1 混淆
     * 京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领,
     */
    PLATE_NUMBER(Pattern.compile(
        "^(([京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领][A-Z]((\\d{5}[A-HJK])|([A-HJK]([A-HJ-NP-Z\\d])\\d{4})))|([京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领]\\d{3}\\d{1,3}[领])|([京津冀黑吉辽鄂豫皖苏浙沪云贵川渝陕甘宁青藏新鲁晋蒙闽赣湘粤桂琼使领][A-Z][A-HJ-NP-Z\\d]{4}[A-HJ-NP-Z\\d挂学警港澳使领]))$")),
    /**
     * RGB 6位色号
     */
    RGB_COLOR6(Pattern.compile("^#[0-9a-fA-F]{6}$")),
    /**
     * RGB 3位色号
     */
    RGB_COLOR3(Pattern.compile("^#[0-9a-fA-F]{3}$")),
    /**
     * 日期校验
     */
    DATE(Pattern.compile("^(\\d{2,4})([\\-/年]?)(\\d{1,2})([\\-/.月]?)(\\d{1,2})日?$")),
    DATE_年月日(Pattern.compile("^\\d{4}年\\d{2}月\\d{2}日$")),
    DATE_yyyy_MM_dd(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$")),
    DATE_MM$dd$yyyy(Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$")),
    DATE_dd$MM$yyyy(Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$")),
    DATE_M$d$yyyy(Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$")),
    DATE_d$M$yyyy(Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$")),
    /**
     * 时间校验
     */
    TIME(Pattern.compile("^(\\d{1,2}:\\d{1,2}(:\\d{1,2})?)|(\\d{1,2}时\\d{1,2}分(\\d{1,2}秒)?)$")),
    /**
     * 纳税人识别号
     * Ref: http://www.qilin668.com/a/5e78a58c99d3cl5.html
     */
    TAXPAYER_CODE(Pattern.compile("^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$")),
    /**
     * 十六进制字符串
     */
    HEX(Pattern.compile("^[a-f0-9]+$", Pattern.CASE_INSENSITIVE)),
    /**
     * 银联卡号
     */
    UNION_PAY_CARD_NO(Pattern.compile("^62[0-9]{14}(\\d{3})?$")),
    ;

    private final static class Cached {

        final static Table<String, Integer, Pattern> CACHE = TableImpl.newWeakHashTable();
    }

    private final Pattern pattern;

    public final Predicate not;

    Patterns(Pattern pattern) {
        this.pattern = pattern;
        this.not = this.negate();
    }

    /**
     * 获取当前{@code Pattern}
     *
     * @return Pattern
     */
    public Pattern getPattern() { return pattern; }

    /**
     * 检查字符串是否匹配当前正则表达式规则
     *
     * @param str 待测字符串
     *
     * @return 检查通过返回 true，否则返回 false
     */
    @Override
    public boolean test(CharSequence str) { return getPattern().matcher(str).matches(); }

    /**
     * 要求字符串必须匹配当前正在表达式
     *
     * @param str 待测字符串
     * @param <C> 字符串类型
     *
     * @return 如果能正确匹配，返回字符串本身
     *
     * @throws RequireValidateException 如果不能匹配抛出异常
     */
    public <C extends CharSequence> C requireMatches(C str) {
        return requireMatches(str, "Invalid input string, require matches of: {},\n\t\t but got: {}.");
    }

    /**
     * 要求字符串必须匹配当前正在表达式
     *
     * @param str     待测字符串
     * @param message 自定义错误消息模板
     * @param <C>     字符串类型
     *
     * @return 如果能正确匹配，返回字符串本身
     *
     * @throws RequireValidateException 如果不能匹配抛出异常
     */
    public <C extends CharSequence> C requireMatches(C str, String message) {
        if (test(str)) {
            return str;
        }
        throw new RequireValidateException(message, getPattern().toString(), str);
    }

    /**
     * 编译正则表达式
     *
     * @param regex 表达式模式字符串
     *
     * @return 编译后的匹配器
     */
    public static Pattern of(String regex) { return Pattern.compile(regex); }

    /**
     * 编译正则表达式
     *
     * @param regex 表达式模式字符串
     * @param flags 标记
     *
     * @return 编译后的匹配器
     */
    public static Pattern of(String regex, int flags) { return Pattern.compile(regex, flags); }

    /**
     * 从缓存中查找或重新编译并缓存模式匹配器
     *
     * @param regex 正则表达式字符串
     *
     * @return Pattern
     */
    public static Pattern find(String regex) { return find(regex, 0); }

    /**
     * 从缓存中查找或重新编译并缓存模式匹配器
     *
     * @param regex 正则表达式字符串
     * @param flags 标记
     *
     * @return Pattern
     */
    public static Pattern find(String regex, int flags) {
        Pattern pattern = Cached.CACHE.get(regex, flags);
        if (pattern == null) {
            pattern = of(regex, flags);
            synchronized (Patterns.class) {
                Cached.CACHE.put(regex, flags, pattern);
            }
        }
        return pattern;
    }
}
