package com.moon.core.util;

import com.moon.core.enums.IntTesters;
import com.moon.core.enums.Patterns;
import com.moon.core.enums.Testers;
import com.moon.core.lang.CharUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.moon.core.enums.Patterns.*;

/**
 * 检测类
 * <ul>
 *     <li>本类中所有方法均以“is”作为前缀，作“是否”之义；</>
 *     <li>本类中所有方法第一个参数都是待测数据，从第二个参数开始后面的参数作为检测条件的一部分；</>
 * </ul>
 *
 * @author moonsky
 * @see Patterns
 * @see Testers
 * @see IntTesters
 * @see StringUtil
 */
@SuppressWarnings("all")
public abstract class TestUtil {

    protected TestUtil() { ThrowUtil.noInstanceError(); }

    /**
     * 验证数据是否为 true
     *
     * @param data 待测数据
     *
     * @return 如果数据 data 是 true 返回 true，否则返回 false
     */
    public final static boolean isTrue(Object data) { return Boolean.TRUE.equals(data); }

    /**
     * 验证数据是否为 false
     *
     * @param data 待测数据
     *
     * @return 如果数据 data 是 false 返回 true，否则返回 false
     */
    public final static boolean isFalse(Object data) { return Boolean.FALSE.equals(data); }

    /**
     * 字符串是否匹配正则表达式模式
     *
     * @param str   待测字符串
     * @param regex 正则表达式
     *
     * @return 符合匹配返回 true，否则返回 false
     */
    public final static boolean isMatchOf(CharSequence str, String regex) {
        return Patterns.find(regex).matcher(str).matches();
    }

    /**
     * 字符串是否匹配正则表达式模式
     *
     * @param str   待测字符串
     * @param regex 正则表达式
     * @param flags 匹配标记
     *
     * @return 符合匹配返回 true，否则返回 false
     */
    public final static boolean isMatchOf(CharSequence str, String regex, int flags) {
        return Patterns.find(regex, flags).matcher(str).matches();
    }

    /**
     * 字符串是否匹配正则表达式模式
     *
     * @param str     待测字符串
     * @param pattern 表达式模式
     *
     * @return 匹配成功返回 true，否则返回 false
     *
     * @see Patterns
     */
    public final static boolean isMatchOf(CharSequence str, Pattern pattern) {
        return str != null && pattern != null && pattern.matcher(str).matches();
    }

    /**
     * 数据是否符合自定义判断条件
     *
     * @param data   待测数据
     * @param tester 判断条件
     * @param <T>    待测数据类型
     *
     * @return 当数据符合指定条件时，返回 true，否则返回 false
     *
     * @see Testers
     * @see Patterns
     */
    public final static <T> boolean isMatchOf(T data, Predicate<? super T> tester) {
        return tester != null && tester.test(data);
    }

    /**
     * 验证 null 值
     *
     * @param data 待测数据
     *
     * @return 如果数据 data 是 null 返回 true，否则返回 false
     */
    public final static boolean isNull(Object data) { return data == null; }

    /**
     * 验证非 null 值
     *
     * @param data 待测数据
     *
     * @return 如果数据 data 不是 null 返回 true，否则返回 false
     */
    public final static boolean isNotNull(Object data) { return data != null; }

    /**
     * 字符串长度
     *
     * @param str    待测字符串
     * @param length 期望字符串的长度
     *
     * @return 当字符串长度等于 length 或字符串为 null 切 length==0 时返回 true，否则返回 false
     */
    public final static boolean isLengthOf(CharSequence str, int length) {
        return (str != null && str.length() == length) || (str == null && length == 0);
    }

    /**
     * 字符串最小长度
     *
     * @param str    待测字符串
     * @param minLen 最小长度（包含）
     *
     * @return 当字符串最少 minLen 个字符时返回 true，否则返回 false
     */
    public final static boolean isLengthAtLeastOf(CharSequence str, int minLen) {
        return (str != null && str.length() >= minLen) || (minLen == 0 && str == null);
    }

    /**
     * 字符串最大长度
     *
     * @param str    待测字符串
     * @param maxLen 最大长度（包含）
     *
     * @return 当字符串最多 minLen 个字符时返回 true，否则返回 false
     */
    public final static boolean isLengthAtMostOf(CharSequence str, int maxLen) {
        return (str != null && str.length() < maxLen) || (maxLen == 0 && str == null);
    }

    /**
     * 字符串长度是否在指定长度区间内
     *
     * @param str    待测字符串
     * @param minLen 最小长度（包含）
     * @param maxLen 最大长度（包含）
     *
     * @return 当字符串长度在区间内时返回 true，否则返回 false；空字符串默认长度为 0
     */
    public final static boolean isLengthBetween(CharSequence str, int minLen, int maxLen) {
        int length = str == null ? 0 : str.length();
        return length >= minLen && length <= maxLen;
    }

    /**
     * 验证数字
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isDigit(CharSequence str) { return StringUtil.isAllMatches(str, CharUtil::isDigit); }

    /**
     * 验证数字
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isNumeric(CharSequence str) { return isDigit(str); }

    /**
     * 验证字母
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isLetter(CharSequence str) {
        return StringUtil.isAllMatches(str, Character::isLetter);
    }

    /**
     * 验证小写字母
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isLower(CharSequence str) {
        return StringUtil.isAllMatches(str, Character::isLowerCase);
    }

    /**
     * 验证大写字母
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isUpper(CharSequence str) {
        return StringUtil.isAllMatches(str, Character::isUpperCase);
    }

    /**
     * 两个对象是否相等
     *
     * @param o1 待测对象 1
     * @param o2 待测对象 2
     *
     * @return true: 相等; false: 不想等
     */
    public final static boolean isEquals(Object o1, Object o2) { return Objects.equals(o1, o2); }

    /**
     * 验证居民身份证号
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isResidentID18(CharSequence str) { return RESIDENT_ID_18.test(str); }

    /**
     * 验证电子邮箱
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isEmail(CharSequence str) { return EMAIL.test(str); }

    /**
     * 是否是 IP v4 地址
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isIPV4(CharSequence str) { return IPV4.test(str); }

    /**
     * 是否是 IP v6 地址
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isIPV6(CharSequence str) { return IPV6.test(str); }

    /**
     * 字符串是否是一个 URL 资源
     *
     * @param str 待测字符串
     *
     * @return 当字符串能被 URL 正常识别时，返回 true，否则返回 false
     */
    public final static boolean isURL(CharSequence str) {
        if (str == null) {
            return false;
        }
        try {
            new URL(str.toString());
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * 是否是 MAC 地址
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isMacAddress(CharSequence str) { return MAC_ADDRESS.test(str); }

    /**
     * 验证常用中文汉字
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isChineseWords(CharSequence str) { return CHINESE_WORDS.test(str); }

    /**
     * 验证 11 位手机号
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isChineseMobile(CharSequence str) { return CHINESE_MOBILE.test(str); }

    /**
     * 验证是否是中国 6 为邮编
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isChineseZipCode(CharSequence str) { return CHINESE_ZIP_CODE.test(str); }

    /**
     * 检测字符串是否是日期字符串
     * 如：yyyy-MM-dd，yyyy年MM月dd日，yyyy/MM/dd
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isDateString(CharSequence str) { return DATE.test(str); }

    /**
     * 检测字符串是否是时间字符串
     * 如：HH:mm:ss，HH时mm分ss秒，HH:mm，HH时mm分
     *
     * @param str 待测字符串
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public final static boolean isTimeString(CharSequence str) { return TIME.test(str); }

    /**
     * 是否是中国车牌号
     *
     * @param str 待测车牌号字符串
     *
     * @return 如果是符合规范的车牌号字符串返回 true，否则返回 false
     */
    public final static boolean isPlateNumber(CharSequence str) { return PLATE_NUMBER.test(str); }

    /**
     * 是否是正确的纳税人税号
     *
     * @param str 待测税号字符串
     *
     * @return 如果是符合规范的纳税人识别号字符串返回 true，否则返回 false
     */
    public final static boolean isTaxpayerCode(CharSequence str) { return TAXPAYER_CODE.test(str); }

    /**
     * 是否是 3 位或 6 位 RGB 颜色色号（包含前面“#”开头）
     *
     * @param str 待测色号字符串
     *
     * @return 如果验证通过返回 true，否则返回 false
     */
    public final static boolean isColorValue(CharSequence str) {
        return RGB_COLOR6.test(str) || RGB_COLOR3.test(str);
    }

    /**
     * 当前数值是否小于目标值
     *
     * @param value 待测值
     * @param max   最大值（不含）
     *
     * @return 当 value < value 时返回 true，否则返回 false
     */
    public final static boolean isLtOf(int value, int max) { return value < max; }

    /**
     * 当前数值是否不大于目标值
     *
     * @param value 待测值
     * @param max   最大值（含）
     *
     * @return 当 value <= value 时返回 true，否则返回 false
     */
    public final static boolean isLeOf(int value, int max) { return value <= max; }

    /**
     * 当前数值是否大于目标值
     *
     * @param value 待测值
     * @param min   最小值（不含）
     *
     * @return 当 value > value 时返回 true，否则返回 false
     */
    public final static boolean isGtOf(int value, int min) { return value > min; }

    /**
     * 当前数值是否不小于目标值
     *
     * @param value 待测值
     * @param min   最小值（含）
     *
     * @return 当 value >= value 时返回 true，否则返回 false
     */
    public final static boolean isGeOf(int value, int min) { return value >= min; }

    /**
     * 当前数值是否和目标数值相等
     *
     * @param value 当前数值
     * @param that  目标数值
     *
     * @return value == that
     */
    public final static boolean isEqOf(int value, int that) { return value == that; }

    /**
     * 当前数值是否和目标数值不相等
     *
     * @param value 当前数值
     * @param that  目标数值
     *
     * @return value != that
     */
    public final static boolean isNotOf(int value, int that) { return value != that; }

    /**
     * 当前数值是否小于目标值
     *
     * @param value 待测值
     * @param max   最大值（不含）
     *
     * @return 当 value < value 时返回 true，否则返回 false
     */
    public final static boolean isLtOf(long value, long max) { return value < max; }

    /**
     * 当前数值是否不大于目标值
     *
     * @param value 待测值
     * @param max   最大值（含）
     *
     * @return 当 value <= value 时返回 true，否则返回 false
     */
    public final static boolean isLeOf(long value, long max) { return value <= max; }

    /**
     * 当前数值是否大于目标值
     *
     * @param value 待测值
     * @param min   最小值（不含）
     *
     * @return 当 value > value 时返回 true，否则返回 false
     */
    public final static boolean isGtOf(long value, long min) { return value > min; }

    /**
     * 当前数值是否不小于目标值
     *
     * @param value 待测值
     * @param min   最小值（含）
     *
     * @return 当 value >= value 时返回 true，否则返回 false
     */
    public final static boolean isGeOf(long value, long min) { return value >= min; }

    /**
     * 当前数值是否和目标数值相等
     *
     * @param value 当前数值
     * @param that  目标数值
     *
     * @return value == that
     */
    public final static boolean isEqOf(long value, long that) { return value == that; }

    /**
     * 当前数值是否和目标数值不相等
     *
     * @param value 当前数值
     * @param that  目标数值
     *
     * @return value != that
     */
    public final static boolean isNotOf(long value, long that) { return value != that; }

    /**
     * 当前数值是否小于目标值
     *
     * @param value 待测值
     * @param max   最大值（不含）
     *
     * @return 当 value < value 时返回 true，否则返回 false
     */
    public final static boolean isLtOf(double value, double max) { return value < max; }

    /**
     * 当前数值是否不大于目标值
     *
     * @param value 待测值
     * @param max   最大值（含）
     *
     * @return 当 value <= value 时返回 true，否则返回 false
     */
    public final static boolean isLeOf(double value, double max) { return value <= max; }

    /**
     * 当前数值是否大于目标值
     *
     * @param value 待测值
     * @param min   最小值（不含）
     *
     * @return 当 value > value 时返回 true，否则返回 false
     */
    public final static boolean isGtOf(double value, double min) { return value > min; }

    /**
     * 当前数值是否不小于目标值
     *
     * @param value 待测值
     * @param min   最小值（含）
     *
     * @return 当 value >= value 时返回 true，否则返回 false
     */
    public final static boolean isGeOf(double value, double min) { return value >= min; }

    /**
     * 当前数值是否和目标数值相等
     *
     * @param value 当前数值
     * @param that  目标数值
     *
     * @return value == that
     */
    public final static boolean isEqOf(double value, double that) { return value == that; }

    /**
     * 当前数值是否和目标数值不相等
     *
     * @param value 当前数值
     * @param that  目标数值
     *
     * @return value != that
     */
    public final static boolean isNotOf(double value, double that) { return value != that; }

    /**
     * 字符串是否为空
     *
     * @param str 待测字符串
     *
     * @return 当字符串为 null 或长度为 0 时返回 true，否则返回 false
     *
     * @see StringUtil#isEmpty(CharSequence)
     */
    public final static boolean isEmpty(CharSequence str) { return StringUtil.isEmpty(str); }

    /**
     * 字符串是否不为空
     *
     * @param str 待测字符串
     *
     * @return 当字符串不为 null 切长度大于 0 时返回 true，否则返回 false
     */
    public final static boolean isNotEmpty(CharSequence str) { return !isEmpty(str); }

    /**
     * 字符串是否是空白字符串，即 str == null 或 str.length() == 0 或字符串所有字符均是空白字符
     *
     * @param str 待测字符串
     *
     * @return 当符合空白字符串条件时返回 true，否则返回 false
     */
    public final static boolean isBlank(CharSequence str) { return StringUtil.isBlank(str); }

    /**
     * 字符串是否是非空白字符串，字符串至少包含一个非空白字符
     *
     * @param str 待测字符串
     *
     * @return 当符合非空白字符串条件时返回 true，否则返回 false
     */
    public final static boolean isNotBlank(CharSequence str) { return !isBlank(str); }

    /**
     * 集合是否为空
     *
     * @param collect 待测集合
     *
     * @return 当集合为 null 或长度为 0 时返回 true，否则返回 false
     *
     * @see StringUtil#isEmpty(CharSequence)
     */
    public final static boolean isEmpty(Collection<?> collect) { return CollectUtil.isEmpty(collect); }

    /**
     * 集合是否不为空
     *
     * @param collect 待测集合
     *
     * @return 当集合不为 null 切长度大于 0 时返回 true，否则返回 false
     */
    public final static boolean isNotEmpty(Collection<?> collect) { return !isEmpty(collect); }

    /**
     * Map 是否为空
     *
     * @param map 待测Map
     *
     * @return 当 Map 为 null 或长度为 0 时返回 true，否则返回 false
     *
     * @see StringUtil#isEmpty(CharSequence)
     */
    public final static boolean isEmpty(Map<?, ?> map) { return MapUtil.isEmpty(map); }

    /**
     * Map 是否不为空
     *
     * @param map 待测Map
     *
     * @return 当 Map 不为 null 切长度大于 0 时返回 true，否则返回 false
     */
    public final static boolean isNotEmpty(Map<?, ?> map) { return !isEmpty(map); }

    /**
     * 字符串内所有字符是否都符合条件
     *
     * @param cs     待测字符串
     * @param tester 依次接受 cs 中的字符为参数，返回是否符合条件
     *
     * @return 全部都符合条件返回 true
     *
     * @see IntTesters
     */
    public final static boolean isAllMatches(CharSequence cs, IntPredicate tester) {
        return StringUtil.isAllMatches(cs, tester);
    }

    /**
     * 字符串内所有字符是否都符合条件
     *
     * @param cs     待测字符串
     * @param tester 依次接受 cs 中的字符为参数，返回是否符合条件
     *
     * @return 至少有一个符号条件返回 true
     *
     * @see IntTesters
     */
    public final static boolean isAnyMatches(CharSequence cs, IntPredicate tester) {
        return StringUtil.isAnyMatches(cs, tester);
    }

    /**
     * 测试集合中所有项符合检查条件
     *
     * @param iterable 待检测集合
     * @param tester   检查条件
     * @param <T>      数据项类型
     *
     * @return 当集合每个单项都符合条件时返回 true，否则返回 false；
     * 当集合为 null 或不包含任何项时，返回 false。
     */
    public final static <T> boolean isAllMatches(Iterable<T> iterable, Predicate<? super T> tester) {
        return isAllMatches(iterable, tester, false);
    }

    /**
     * 测试集合中所有项符合检查条件
     *
     * @param iterable       待检测集合
     * @param tester         检查条件
     * @param defaultIfEmpty 当集合为 null 或不包含任何元素时，返回默认值
     * @param <T>            数据项类型
     *
     * @return 当集合每个单项都符合条件时返回 true，否则返回 false；
     * 当集合为 null 或不包含任何项时，返回{@code defaultIfEmpty}。
     */
    public final static <T> boolean isAllMatches(
        Iterable<T> iterable, Predicate<? super T> tester, boolean defaultIfEmpty
    ) {
        if (iterable != null) {
            int index = 0;
            for (T item : iterable) {
                if (!tester.test(item)) {
                    return false;
                }
                index++;
            }
            return index > 0 || defaultIfEmpty;
        } else {
            return defaultIfEmpty;
        }
    }

    /**
     * 测试数组中所有项符合检查条件
     *
     * @param arr    待检测数组
     * @param tester 检查条件
     * @param <T>    数据项类型
     *
     * @return 当数组每个单项都符合条件时返回 true，否则返回 false；
     * 当数组为 null 或不包含任何项时，返回 false。
     */
    public final static <T> boolean isAllMatches(T[] arr, Predicate<? super T> tester) {
        return isAllMatches(arr, tester, false);
    }

    /**
     * 测试数组中所有项符合检查条件
     *
     * @param arr            待检测数组
     * @param tester         检查条件
     * @param defaultIfEmpty 当数组为 null 或不包含任何元素时，返回默认值
     * @param <T>            数据项类型
     *
     * @return 当数组每个单项都符合条件时返回 true，否则返回 false；
     * 当数组为 null 或不包含任何项时，返回{@code defaultIfEmpty}。
     */
    public final static <T> boolean isAllMatches(T[] arr, Predicate<? super T> tester, boolean defaultIfEmpty) {
        if (arr != null) {
            int index = 0;
            for (T item : arr) {
                if (!tester.test(item)) {
                    return false;
                }
                index++;
            }
            return index > 0 || defaultIfEmpty;
        } else {
            return defaultIfEmpty;
        }
    }

    /**
     * 测试集合中至少有一项符合检查条件
     *
     * @param iterable 待检测集合
     * @param tester   检查条件
     * @param <T>      数据项类型
     *
     * @return 当集合每个单项都符合条件时返回 true，否则返回 false；
     * 当集合为 null 或不包含任何项时，返回 false。
     */
    public final static <T> boolean isAnyMatches(Iterable<T> iterable, Predicate<? super T> tester) {
        return isAnyMatches(iterable, tester, false);
    }

    /**
     * 测试集合中至少有一项符合检查条件
     *
     * @param iterable       待检测集合
     * @param tester         检查条件
     * @param defaultIfEmpty 当集合为 null 或不包含任何元素时，返回默认值
     * @param <T>            数据项类型
     *
     * @return 当集合每个单项都符合条件时返回 true，否则返回 false；
     * 当集合为 null 或不包含任何项时，返回{@code defaultIfEmpty}。
     */
    public final static <T> boolean isAnyMatches(
        Iterable<T> iterable, Predicate<? super T> tester, boolean defaultIfEmpty
    ) {
        if (iterable != null) {
            int index = 0;
            for (T item : iterable) {
                if (tester.test(item)) {
                    return true;
                }
                index++;
            }
            return index == 0 && defaultIfEmpty;
        } else {
            return defaultIfEmpty;
        }
    }

    /**
     * 测试数组中至少有一项符合检查条件
     *
     * @param arr    待检测数组
     * @param tester 检查条件
     * @param <T>    数据项类型
     *
     * @return 只要数组中有一项符合条件就返回 true，否则返回 false；
     * 当数组为 null 或不包含任何项时，返回 false。
     */
    public final static <T> boolean isAnyMatches(T[] arr, Predicate<? super T> tester) {
        return isAnyMatches(arr, tester, false);
    }

    /**
     * 测试数组中至少有一项符合检查条件
     *
     * @param arr            待检测数组
     * @param tester         检查条件
     * @param defaultIfEmpty 当数组为 null 或不包含任何元素时，返回默认值
     * @param <T>            数据项类型
     *
     * @return 只要数组中有一项符合条件就返回 true，否则返回 false；
     * 当数组为 null 或不包含任何项时，返回{@code defaultIfEmpty}。
     */
    public final static <T> boolean isAnyMatches(T[] arr, Predicate<? super T> tester, boolean defaultIfEmpty) {
        if (arr != null) {
            int index = 0;
            for (T item : arr) {
                if (tester.test(item)) {
                    return true;
                }
                index++;
            }
            return index == 0 && defaultIfEmpty;
        } else {
            return defaultIfEmpty;
        }
    }

    /**
     * 测试集合中所有项都不符合检查条件
     *
     * @param iterable 待检测集合
     * @param tester   检查条件
     * @param <T>      数据项类型
     *
     * @return 当集合每个单项都不符合条件时返回 true，否则返回 false；
     * 当集合为 null 或不包含任何项时，返回 true。
     */
    public final static <T> boolean isNonMatches(Iterable<T> iterable, Predicate<? super T> tester) {
        return isNonMatches(iterable, tester, true);
    }

    /**
     * 测试集合中所有项都不符合检查条件
     *
     * @param iterable       待检测集合
     * @param tester         检查条件
     * @param defaultIfEmpty 当集合为 null 或不包含任何元素时，返回默认值
     * @param <T>            数据项类型
     *
     * @return 当集合每个单项都不符合条件时返回 true，否则返回 false；
     * 当集合为 null 或不包含任何项时，返回{@code defaultIfEmpty}。
     */
    public final static <T> boolean isNonMatches(
        Iterable<T> iterable, Predicate<? super T> tester, boolean defaultIfEmpty
    ) {
        if (iterable != null) {
            int index = 0;
            for (T item : iterable) {
                if (tester.test(item)) {
                    return false;
                }
                index++;
            }
            return index > 0 || defaultIfEmpty;
        } else {
            return defaultIfEmpty;
        }
    }

    /**
     * 测试集合中所有项都不符合检查条件
     *
     * @param arr    待检测集合
     * @param tester 检查条件
     * @param <T>    数据项类型
     *
     * @return 当集合每个单项都不符合条件时返回 true，否则返回 false
     * 当数组为 null 或不包含任何项时，返回 true。
     */
    public final static <T> boolean isNonMatches(T[] arr, Predicate<? super T> tester) {
        return isNonMatches(arr, tester, true);
    }

    /**
     * 测数组合中所有项都不符合检查条件
     *
     * @param arr            待检测数组
     * @param tester         检查条件
     * @param defaultIfEmpty 当数组为 null 或不包含任何元素时，返回默认值
     * @param <T>            数据项类型
     *
     * @return 当集合每个单项都不符合条件时返回 true，否则返回 false；
     * 当数组为 null 或不包含任何项时，返回{@code defaultIfEmpty}。
     */
    public final static <T> boolean isNonMatches(T[] arr, Predicate<? super T> tester, boolean defaultIfEmpty) {
        if (arr != null) {
            int index = 0;
            for (T item : arr) {
                if (tester.test(item)) {
                    return false;
                }
                index++;
            }
            return index > 0 || defaultIfEmpty;
        } else {
            return defaultIfEmpty;
        }
    }

    /**
     * 是否是扩展数字：正实数、负实数、八进制、十进制、十六进制
     *
     * @param str 待测数字字符串
     *
     * @return 符合验证时返回 true，否则返回 false
     */
    @SuppressWarnings("all")
    public final static boolean isGeneralNumber(CharSequence str) {
        int length = str == null ? 0 : str.length();
        if (length > 0) {
            String string = str.toString();
            boolean point = false;
            int i = 0;
            char ch = string.charAt(i++);
            if (ch == 46) {
                point = true;
            } else if (!((ch > 47 && ch < 58) || (ch > 64 && ch < 71) || (ch > 96 && ch < 103))) {
                if (ch != 45 || length == i) {
                    return false;
                }
            }

            ch = string.charAt(i++);
            if (ch == 48) {
                if (length > i) {
                    ch = string.charAt(i++);
                    if (ch == 88 || ch == 120) {
                        for (; i < length; i++) {
                            ch = string.charAt(i);
                            if ((ch > 47 && ch < 58) || (ch > 64 && ch < 71) || (ch > 96 && ch < 103)) {
                                continue;
                            } else {
                                if (ch != 46) {
                                    return false;
                                } else {
                                    if (point) {
                                        return false;
                                    } else {
                                        point = true;
                                    }
                                }
                            }
                        }
                        return true;
                    }
                } else {
                    return true;
                }
            }
            for (; i < length; i++) {
                ch = string.charAt(i);
                if (ch > 57 || ch < 48) {
                    if (ch == 46) {
                        if (point) {
                            return false;
                        } else {
                            point = true;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 是否是一个十进制小数（包括正数和负数）
     *
     * @param str 待测数字字符串
     *
     * @return 符合验证时返回 true，否则返回 false
     */
    @SuppressWarnings("all")
    public final static boolean isDoubleValue(CharSequence str) {
        int length = str == null ? 0 : str.length();
        if (length > 0) {
            String string = str.toString();
            boolean point = false;

            int i = 0, ch = string.charAt(0);
            if (ch == 45) {
                i = 1;
            } else if (ch == 46) {
                i = 1;
                point = true;
            }

            if (length > i) {
                for (; i < length; i++) {
                    ch = string.charAt(i);
                    if (ch > 57 || ch < 48) {
                        if (ch == 46) {
                            if (!point) {
                                point = true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }


    /**
     * 是否是一个整数（包括正整数和负整数，只支持十进制数）
     *
     * @param str 待测数字字符串
     *
     * @return 符合验证时返回 true，否则返回 false
     */
    public final static boolean isIntegerValue(CharSequence str) {
        int len = str == null ? 0 : str.length();
        if (len > 0) {
            String string = str.toString();
            int i = 0;

            char ch = string.charAt(i++);
            if (ch > 57 || ch < 48) {
                if (ch != 45 || len == i) {
                    return false;
                }
            }

            while (i < len) {
                ch = string.charAt(i++);
                if (ch > 57 || ch < 48) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
