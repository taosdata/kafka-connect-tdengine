package com.moon.core.util;

import com.moon.core.enums.IntTesters;
import com.moon.core.enums.Patterns;
import com.moon.core.enums.Testers;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.validator.CollectValidator;
import com.moon.core.util.validator.MapValidator;
import com.moon.core.util.validator.Validator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 验证类
 * <ul>
 *     <li>类中所有方法均已“require”作为前缀，作“要求”之义，不符合要求就是错误；参考自{@link Objects#requireNonNull(Object)}命名</li>
 *     <li>类中所有方法第一个参数均是待测数据，也是你希望在符合条件时的返回值，从第二个参数开始作为检测条件的一部分</li>
 *     <li>要求数据符合指定检测条件，如果符合要求均返回数据本身，否则统一抛出异常{@link RequireValidateException}</li>
 *     <li>类中的方法一部分有默认消息，另一部分可以自定义消息模板</li>
 *     <li>可自定义消息模板的方法最后一个参数是消息模板，同时消息模板可通过占位符“{}”依次接收待测数据和检测条件为参数</li>
 * </ul>
 * <p>
 * （在写这个类的时候，尝试尽可能将注释写完整一点，但是实际完成下来，个人觉得这样效果一般，因为这样在电脑上一屏显示的内容不够多；
 * 而且这个类的逻辑简单，语义清晰，命名可读性也比较高，没必要写完整的注释，以后其他类在保证可读性基础上根据情况写）
 *
 * @author moonsky
 * @see Patterns
 * @see Testers
 * @see IntTesters
 * @see StringUtil
 */
@SuppressWarnings("all")
public abstract class ValidateUtil extends TestUtil {

    /**
     * 集合多条件验证器
     *
     * @param collect 待验证集合
     * @param <C>     集合类型
     * @param <E>     集合数据项类型
     *
     * @return 集合验证器
     */
    public final static <C extends Collection<E>, E> CollectValidator<C, E> ofCollect(C collect) {
        return CollectValidator.of(collect);
    }

    /**
     * 集合多条件验证器，集合对象可为空
     *
     * @param collect 待验证集合
     * @param <C>     集合类型
     * @param <E>     集合数据项类型
     *
     * @return 集合验证器
     */
    public final static <C extends Collection<E>, E> CollectValidator<C, E> ofNullableCollect(C collect) {
        return CollectValidator.ofNullable(collect);
    }

    /**
     * Map 多条件验证器
     *
     * @param map 待验证 Map
     * @param <M> Map 类型
     * @param <K> Map 键类型
     * @param <V> Map 值类型
     *
     * @return Map 验证器
     */
    public final static <M extends Map<K, V>, K, V> MapValidator<M, K, V> ofMap(M map) { return MapValidator.of(map); }

    /**
     * Map 多条件验证器，Map 对象可为空
     *
     * @param map 待验证 Map
     * @param <M> Map 类型
     * @param <K> Map 键类型
     * @param <V> Map 值类型
     *
     * @return Map 验证器
     */
    public final static <M extends Map<K, V>, K, V> MapValidator<M, K, V> ofNullableMap(M map) {
        return MapValidator.ofNullable(map);
    }

    /**
     * 对象多条件验证器
     *
     * @param data 待验证对象
     * @param <T>  对象数据类型
     *
     * @return 验证器
     */
    public final static <T> Validator<T> of(T data) { return Validator.of(data); }

    /**
     * 对象多条件验证器，数据对象可为空
     *
     * @param data 待验证对象
     * @param <T>  对象数据类型
     *
     * @return 验证器
     */
    public final static <T> Validator<T> ofNullable(T data) { return Validator.ofNullable(data); }

    protected ValidateUtil() { }

    /**
     * 要求数据为 true
     *
     * @param obj 待测数据
     * @param <T> 数据类型（通常是 Boolean）
     *
     * @return 当数据 obj == true 时，返回数据本身 true；
     *
     * @throws RequireValidateException 当检测数据为 false 或其他值时抛出异常
     */
    public final static <T> T requireTrue(T obj) {
        return requireTrue(obj, "Invalid data, require 'true', but got: {}");
    }

    /**
     * 要求数据为 true
     *
     * @param obj     待测数据
     * @param message 自定义消息模板
     * @param <T>     数据类型（通常是 Boolean）
     *
     * @return 当数据 obj == true 时，返回数据本身 true；
     *
     * @throws RequireValidateException 当检测数据为 false 或其他值时抛出异常
     *                                  异常消息由调用方自定义，并可用“{}”占位符接收入参字符串
     */
    public final static <T> T requireTrue(T obj, String message) {
        if (isTrue(obj)) {
            return obj;
        }
        throw new RequireValidateException(message, obj);
    }

    /**
     * 要求数据为 false
     *
     * @param obj 待测数据
     * @param <T> 数据类型（通常是 Boolean）
     *
     * @return 当数据 obj == false 时，返回数据本身 false；
     *
     * @throws RequireValidateException 当检测数据为 true 或其他值时抛出异常
     */
    public final static <T> T requireFalse(T obj) {
        return requireFalse(obj, "Invalid data, require 'false', but got: {}");
    }

    /**
     * 要求数据为 false
     *
     * @param obj     待测数据
     * @param message 自定义消息模板
     * @param <T>     数据类型（通常是 Boolean）
     *
     * @return 当数据 obj == false 时，返回数据本身 false；
     *
     * @throws RequireValidateException 当检测数据为 true 或其他值时抛出异常，
     *                                  异常消息有调用方自定义，并可用“{}”占位符接收入参字符串
     */
    public final static <T> T requireFalse(T obj, String message) {
        if (isFalse(obj)) {
            return obj;
        }
        throw new RequireValidateException(message, obj);
    }

    /**
     * 要求字符串匹配指定的正则表达式
     *
     * @param str   待测字符串
     * @param regex 指定正则表达式
     * @param <C>   字符串数据类型
     *
     * @return 如果匹配成功返回 true
     *
     * @throws RequireValidateException 当字符串与正则表达式不匹配时抛出异常
     */
    public final static <C extends CharSequence> C requireMatchOf(C str, String regex) {
        return requireMatchOf(str, regex, "Invalid string: {}, require match of: {}");
    }

    /**
     * 要求字符串匹配指定的正则表达式
     *
     * @param str     待测字符串
     * @param regex   指定正则表达式
     * @param message 指定错误消息模板，可用占位符“{}”依次接受两个入参
     * @param <C>     字符串数据类型
     *
     * @return 如果匹配成功返回 true
     *
     * @throws RequireValidateException 当字符串与正则表达式不匹配时抛出异常，可用占位符“{}”依次接受两个入参
     */
    public final static <C extends CharSequence> C requireMatchOf(C str, String regex, String message) {
        if (isMatchOf(str, regex)) {
            return str;
        }
        throw new RequireValidateException(message, str, regex);
    }

    /**
     * 要求字符串匹配指定的正则表达式
     *
     * @param str   待测字符串
     * @param regex 指定正则表达式
     * @param <C>   字符串数据类型
     *
     * @return 如果匹配成功返回 true
     *
     * @throws RequireValidateException 当字符串与正则表达式不匹配时抛出异常
     */
    public final static <C extends CharSequence> C requireMatchOf(C str, String regex, int flags) {
        return requireMatchOf(str, regex, flags, "Invalid string: {}, require match of: {}");
    }

    /**
     * 要求字符串匹配指定的正则表达式
     *
     * @param str     待测字符串
     * @param regex   指定正则表达式
     * @param message 指定错误消息模板，可用占位符“{}”依次接受三个入参
     * @param <C>     字符串数据类型
     *
     * @return 如果匹配成功返回 true
     *
     * @throws RequireValidateException 当字符串与正则表达式不匹配时抛出异常，可用占位符“{}”依次接受三个入参
     */
    public final static <C extends CharSequence> C requireMatchOf(C str, String regex, int flags, String message) {
        if (isMatchOf(str, regex, flags)) {
            return str;
        }
        throw new RequireValidateException(message, str, regex, flags);
    }

    /**
     * 要求字符串匹配指定的正则表达式
     *
     * @param str     待测字符串
     * @param pattern 指定正则表达式
     * @param <C>     字符串数据类型
     *
     * @return 如果匹配成功返回 true
     *
     * @throws RequireValidateException 当字符串与正则表达式不匹配时抛出异常
     * @see Patterns
     */
    public final static <C extends CharSequence> C requireMatchOf(C str, Pattern pattern) {
        return requireMatchOf(str, pattern, "Invalid string: {}, require match of: {}");
    }

    /**
     * 要求字符串匹配指定的正则表达式
     *
     * @param str     待测字符串
     * @param pattern 指定正则表达式
     * @param message 指定错误消息模板，可用占位符“{}”依次接受两个入参
     * @param <C>     字符串数据类型
     *
     * @return 如果匹配成功返回 true
     *
     * @throws RequireValidateException 当字符串与正则表达式不匹配时抛出异常，可用占位符“{}”依次接受两个入参
     * @see Patterns
     */
    public final static <C extends CharSequence> C requireMatchOf(C str, Pattern pattern, String message) {
        if (isMatchOf(str, pattern)) {
            return str;
        }
        throw new RequireValidateException(message, str, pattern);
    }

    /**
     * 要求数据符合自定义判断条件
     *
     * @param data   待测数据
     * @param tester 自定义判断逻辑
     * @param <T>    数据类型
     *
     * @return 如果匹配成功返回数据本身
     *
     * @throws RequireValidateException 当数据与判断逻辑不匹配时抛出异常
     * @see Testers
     * @see Patterns
     */
    public final static <T> T requireMatchOf(T data, Predicate<? super T> tester) {
        if (isMatchOf(data, tester)) {
            return data;
        }
        throw new RequireValidateException("Invalid input data.");
    }

    /**
     * 要求数据符合自定义判断条件
     *
     * @param data    待测数据
     * @param tester  自定义判断逻辑
     * @param message 自定义消息模板
     * @param <T>     数据类型
     *
     * @return 如果匹配成功返回数据本身
     *
     * @throws RequireValidateException 当数据与判断逻辑不匹配时抛出异常
     * @see Testers
     * @see Patterns
     */
    public final static <T> T requireMatchOf(T data, Predicate<? super T> tester, String message) {
        if (isMatchOf(data, tester)) {
            return data;
        }
        throw new RequireValidateException(message, data);
    }

    /**
     * 要求数据符合自定义判断条件
     *
     * @param data             待测数据
     * @param tester           自定义判断逻辑
     * @param throwableBuilder 自定义异常
     * @param <T>              待测数据类型
     * @param <EX>             自定义异常类型
     *
     * @return 如果匹配成功返回数据本身
     *
     * @throws EX 当数据与判断逻辑不匹配时抛出异常
     */
    public final static <T, EX extends Throwable> T require(
        T data, Predicate<? super T> tester, Function<? super T, EX> throwableBuilder
    ) throws EX {
        if (isMatchOf(data, tester)) {
            return data;
        }
        throw throwableBuilder.apply(data);
    }

    /**
     * 要求非 null 值
     *
     * @param obj 待测数据
     * @param <T> 数据类型
     *
     * @return 当数据 obj 不为 null 时，返回数据本身；
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <T> T requireNotNull(T obj) {
        return requireNotNull(obj, "Invalid data, require not null, but got null.");
    }

    /**
     * 要求非 null 值
     *
     * @param obj     待测数据
     * @param message 自定义消息模板
     * @param <T>     数据类型
     *
     * @return 当数据 obj 不为 null 时，返回数据本身；
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     *                                  异常消息由调用方自定义，并可用“{}”占位符接收入参字符串
     */
    public final static <T> T requireNotNull(T obj, String message) {
        if (isNotNull(obj)) { return obj; }
        throw new RequireValidateException(message, (Object) null);
    }

    /**
     * 要求数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public final static <C extends CharSequence> C requireDigit(C str) {
        return requireDigit(str, "Invalid digit string: {}");
    }


    /**
     * 要求数字
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireDigit(C str, String message) {
        if (isDigit(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireNumeric(C str) { return requireDigit(str); }

    /**
     * 要求数字
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public final static <C extends CharSequence> C requireNumeric(C str, String message) {
        return requireDigit(str, message);
    }

    /**
     * 要求字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireLetter(C str) {
        return requireLetter(str, "Invalid letter string: {}");
    }

    /**
     * 要求字母
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireLetter(C str, String message) {
        if (isLetter(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求小写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireLower(C str) {
        return requireLower(str, "Invalid lower case string: {}");
    }

    /**
     * 要求小写字母
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireLower(C str, String message) {
        if (isLower(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求大写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireUpper(C str) {
        if (isUpper(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid upper case string: " + str);
    }

    /**
     * 要求大写字母
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireUpper(C str, String message) {
        if (isUpper(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求对象等于另一个对象是返回当前对象
     *
     * @param obj      待测对象
     * @param expected 目标对象
     * @param <O>      待测对象数据类型
     *
     * @return 当 obj == expected 或 obj != null && obj.equals(expected) 时，返回 obj
     *
     * @throws RequireValidateException 当不符合验证条件是，抛出异常
     */
    public final static <O> O requireEquals(O obj, Object expected) {
        if (isEquals(obj, expected)) {
            return obj;
        }
        throw new RequireValidateException("Invalid data");
    }

    /**
     * 要求对象等于另一个对象是返回当前对象
     *
     * @param obj      待测对象
     * @param expected 目标对象
     * @param message  目标对象
     * @param <O>      待测对象数据类型
     *
     * @return 当 obj == expected 或 obj != null && obj.equals(expected) 时，返回 obj
     *
     * @throws RequireValidateException 当不符合验证条件是，抛出异常
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <O> O requireEquals(O obj, Object expected, String message) {
        if (isEquals(obj, expected)) {
            return obj;
        }
        throw new RequireValidateException(message, obj, expected);
    }

    /**
     * 要求对象不等于另一个对象是返回当前对象
     *
     * @param obj      待测对象
     * @param expected 目标对象
     * @param <O>      待测对象数据类型
     *
     * @return 当 obj 与 expected 不相等时，返回 obj
     *
     * @throws RequireValidateException 当不符合验证条件是，抛出异常
     */
    public final static <O> O requireNotEquals(O obj, Object expected) {
        if (!isEquals(obj, expected)) {
            return obj;
        }
        throw new RequireValidateException("Invalid data");
    }

    /**
     * 要求对象不等于另一个对象是返回当前对象
     *
     * @param obj      待测对象
     * @param expected 目标对象
     * @param message  目标对象
     * @param <O>      待测对象数据类型
     *
     * @return 当 obj 与 expected 不相等时，返回 obj
     *
     * @throws RequireValidateException 当不符合验证条件是，抛出异常
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <O> O requireNotEquals(O obj, Object expected, String message) {
        if (!isEquals(obj, expected)) {
            return obj;
        }
        throw new RequireValidateException(message, obj, expected);
    }

    /**
     * 要求居民身份证号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireResidentID18(C str) {
        if (isResidentID18(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid resident ID: " + str);
    }

    /**
     * 要求居民身份证号
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireResidentID18(C str, String message) {
        if (isResidentID18(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求常用中文汉字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireChineseWords(C str) {
        if (isChineseWords(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid resident ID: " + str);
    }

    /**
     * 要求常用中文汉字
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireChineseWords(C str, String message) {
        if (isChineseWords(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求中国 11 位手机号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireChineseMobile(C str) {
        if (isChineseMobile(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid resident ID: " + str);
    }

    /**
     * 要求中国 11 位手机号
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireChineseMobile(C str, String message) {
        if (isChineseMobile(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是中国 6 位邮政编码
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireChineseZipCode(C str) {
        if (isChineseZipCode(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid chinese zip code: " + str);
    }

    /**
     * 要求是中国 6 位邮政编码
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireChineseZipCode(C str, String message) {
        if (isChineseZipCode(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是日期字符串
     * 如：yyyy-MM-dd，yyyy年MM月dd日，yyyy/MM/dd
     *
     * @param str 待测字符串
     * @param <C> 字符串类型
     *
     * @return 如果符合检测规则，返回字符串本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireDateString(C str) {
        if (isDateString(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid date string: " + str + "; expected like: yyyy-MM-dd");
    }

    /**
     * 要求是日期字符串
     * 如：yyyy-MM-dd，yyyy年MM月dd日，yyyy/MM/dd
     *
     * @param str 待测字符串
     * @param <C> 字符串类型
     *
     * @return 如果符合检测规则，返回字符串本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireDateString(C str, String message) {
        if (isDateString(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是时间字符串
     * 如：HH:mm:ss，HH时mm分ss秒，HH:mm，HH时mm分
     *
     * @param str 待测字符串
     * @param <C> 字符串类型
     *
     * @return 如果符合检测规则，返回字符串本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireTimeString(C str) {
        if (isTimeString(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid date string: " + str + "; expected like: yyyy-MM-dd");
    }

    /**
     * 要求是时间字符串
     * 如：HH:mm:ss，HH时mm分ss秒，HH:mm，HH时mm分
     *
     * @param str 待测字符串
     * @param <C> 字符串类型
     *
     * @return 如果符合检测规则，返回字符串本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireTimeString(C str, String message) {
        if (isTimeString(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求邮箱地址
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireEmail(C str) {
        if (isEmail(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid email: " + str);
    }

    /**
     * 要求邮箱地址
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireEmail(C str, String message) {
        if (isEmail(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是 IP v4 地址
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireIPV4(C str) {
        if (isIPV4(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid IP v4 address: " + str);
    }

    /**
     * 要求是 IP v4 地址
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireIPV4(C str, String message) {
        if (isIPV4(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是 IP v6 地址
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireIPV6(C str) {
        if (isIPV6(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid IP v4 address: " + str);
    }

    /**
     * 要求是 IP v6 地址
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireIPV6(C str, String message) {
        if (isIPV6(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是 URL 地址
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireURL(C str) {
        if (isURL(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid IP v4 address: " + str);
    }

    /**
     * 要求是 URL 地址
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireURL(C str, String message) {
        if (isURL(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是 MAC 地址
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireMacAddress(C str) {
        if (isMacAddress(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid IP v4 address: " + str);
    }

    /**
     * 要求是 MAC 地址
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireMacAddress(C str, String message) {
        if (isMacAddress(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是否符合规范的中国车牌号
     *
     * @param str 待测车牌号字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requirePlateNumber(C str) {
        if (isPlateNumber(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid plate number: " + str);
    }

    /**
     * 要求是否符合规范的中国车牌号
     *
     * @param str     待测车牌号字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requirePlateNumber(C str, String message) {
        if (isPlateNumber(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是否符合规范的中国纳税人识别号
     *
     * @param str 待测纳税人识别号字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串（纳税人识别号）
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireTaxpayerCode(C str) {
        if (isTaxpayerCode(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid plate number: " + str);
    }

    /**
     * 要求是否符合规范的中国纳税人识别号
     *
     * @param str     待测纳税人识别号字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串（纳税人识别号）
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireTaxpayerCode(C str, String message) {
        if (isTaxpayerCode(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求是 3 位或 6 位 RGB 颜色色号（包含前面“#”开头）
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireColorValue(C str) {
        if (isColorValue(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid plate number: " + str);
    }

    /**
     * 要求是 3 位或 6 位 RGB 颜色色号（包含前面“#”开头）
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireColorValue(C str, String message) {
        if (isColorValue(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求小于某个值
     *
     * @param value 待测值
     * @param max   最大值（不含）
     *
     * @return value < max
     *
     * @throws RequireValidateException 当 value >= max 时
     */
    public final static int requireLtOf(int value, int max) {
        return requireLtOf(value, max, "Invalid value: {}, require less than: {}.");
    }

    /**
     * 要求小于某个值
     *
     * @param value   待测值
     * @param max     最大值（不含）
     * @param message 自定义错误消息模板
     *
     * @return value < max
     *
     * @throws RequireValidateException 当 value >= max 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static int requireLtOf(int value, int max, String message) {
        if (isLtOf(value, max)) {
            return value;
        }
        throw new RequireValidateException(message, value, max);
    }

    /**
     * 要求不大于某个值
     *
     * @param value 待测值
     * @param max   最大值（含）
     *
     * @return value <= max
     *
     * @throws RequireValidateException 当 value > max 时
     */
    public final static int requireLeOf(int value, int max) {
        return requireLeOf(value, max, "Invalid value: {}, require not great than: {}.");
    }

    /**
     * 要求不大于某个值
     *
     * @param value   待测值
     * @param max     最大值（含）
     * @param message 自定义错误消息模板
     *
     * @return value <= max
     *
     * @throws RequireValidateException 当 value > max 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static int requireLeOf(int value, int max, String message) {
        if (isLeOf(value, max)) {
            return value;
        }
        throw new RequireValidateException(message, value, max);
    }

    /**
     * 要求大于某个值
     *
     * @param value 待测值
     * @param min   最小值（不含）
     *
     * @return value > min
     *
     * @throws RequireValidateException 当 value <= min 时
     */
    public final static int requireGtOf(int value, int min) {
        return requireGtOf(value, min, "Invalid value: {}, require great than: {}.");
    }

    /**
     * 要求大于某个值
     *
     * @param value   待测值
     * @param min     最小值（不含）
     * @param message 自定义错误消息模板
     *
     * @return value > min
     *
     * @throws RequireValidateException 当 value <= min 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static int requireGtOf(int value, int min, String message) {
        if (isGtOf(value, min)) {
            return value;
        }
        throw new RequireValidateException(message, value, min);
    }

    /**
     * 要求不小于某个值
     *
     * @param value 待测值
     * @param min   最小值（含）
     *
     * @return value >= max
     *
     * @throws RequireValidateException 当 value < min 时
     */
    public final static int requireGeOf(int value, int min) {
        return requireGeOf(value, min, "Invalid value: {}, require not less than: {}.");
    }

    /**
     * 要求不小于某个值
     *
     * @param value   待测值
     * @param min     最小值（含）
     * @param message 自定义错误消息模板
     *
     * @return value >= max
     *
     * @throws RequireValidateException 当 value < min 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static int requireGeOf(int value, int min, String message) {
        if (isGeOf(value, min)) {
            return value;
        }
        throw new RequireValidateException(message, value, min);
    }

    /**
     * 要求数值和另外一个值相等
     *
     * @param value 待测值
     * @param that  目标值
     *
     * @return value == that
     *
     * @throws RequireValidateException 当 value != that 时
     */
    public final static int requireEqOf(int value, int that) {
        return requireEqOf(value, that, "Invalid value: {}, require: {}.");
    }

    /**
     * 要求数值和另外一个值相等
     *
     * @param value   待测值
     * @param that    目标值
     * @param message 自定义错误消息模板
     *
     * @return value == that
     *
     * @throws RequireValidateException 当 value != that 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static int requireEqOf(int value, int that, String message) {
        if (isEqOf(value, that)) {
            return value;
        }
        throw new RequireValidateException(message, value, that);
    }

    /**
     * 要求数值和另外一个值不相等
     *
     * @param value 待测值
     * @param that  目标值
     *
     * @return value != that
     *
     * @throws RequireValidateException 当 value == that 时
     */
    public final static int requireNotOf(int value, int that) {
        return requireNotOf(value, that, "Invalid value: {}, require not of: {}.");
    }

    /**
     * 要求数值和另外一个值不相等
     *
     * @param value   待测值
     * @param that    目标值
     * @param message 自定义错误消息模板
     *
     * @return value != that
     *
     * @throws RequireValidateException 当 value == that 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static int requireNotOf(int value, int that, String message) {
        if (isNotOf(value, that)) {
            return value;
        }
        throw new RequireValidateException(message, value, that);
    }

    /**
     * 要求小于某个值
     *
     * @param value 待测值
     * @param max   最大值（不含）
     *
     * @return value < max
     *
     * @throws RequireValidateException 当 value >= max 时
     */
    public final static long requireLtOf(long value, long max) {
        return requireLtOf(value, max, "Invalid value: {}, require less than: {}.");
    }

    /**
     * 要求小于某个值
     *
     * @param value   待测值
     * @param max     最大值（不含）
     * @param message 自定义错误消息模板
     *
     * @return value < max
     *
     * @throws RequireValidateException 当 value >= max 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static long requireLtOf(long value, long max, String message) {
        if (isLtOf(value, max)) {
            return value;
        }
        throw new RequireValidateException(message, value, max);
    }

    /**
     * 要求不大于某个值
     *
     * @param value 待测值
     * @param max   最大值（含）
     *
     * @return value <= max
     *
     * @throws RequireValidateException 当 value > max 时
     */
    public final static long requireLeOf(long value, long max) {
        return requireLeOf(value, max, "Invalid value: {}, require not great than: {}.");
    }

    /**
     * 要求不大于某个值
     *
     * @param value   待测值
     * @param max     最大值（含）
     * @param message 自定义错误消息模板
     *
     * @return value <= max
     *
     * @throws RequireValidateException 当 value > max 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static long requireLeOf(long value, long max, String message) {
        if (isLeOf(value, max)) {
            return value;
        }
        throw new RequireValidateException(message, value, max);
    }

    /**
     * 要求大于某个值
     *
     * @param value 待测值
     * @param min   最小值（不含）
     *
     * @return value > min
     *
     * @throws RequireValidateException 当 value <= min 时
     */
    public final static long requireGtOf(long value, long min) {
        return requireGtOf(value, min, "Invalid value: {}, require great than: {}.");
    }

    /**
     * 要求大于某个值
     *
     * @param value   待测值
     * @param min     最小值（不含）
     * @param message 自定义错误消息模板
     *
     * @return value > min
     *
     * @throws RequireValidateException 当 value <= min 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static long requireGtOf(long value, long min, String message) {
        if (isGtOf(value, min)) {
            return value;
        }
        throw new RequireValidateException(message, value, min);
    }

    /**
     * 要求不小于某个值
     *
     * @param value 待测值
     * @param min   最小值（含）
     *
     * @return value >= max
     *
     * @throws RequireValidateException 当 value < min 时
     */
    public final static long requireGeOf(long value, long min) {
        return requireGeOf(value, min, "Invalid value: {}, require not less than: {}.");
    }

    /**
     * 要求不小于某个值
     *
     * @param value   待测值
     * @param min     最小值（含）
     * @param message 自定义错误消息模板
     *
     * @return value >= max
     *
     * @throws RequireValidateException 当 value < min 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static long requireGeOf(long value, long min, String message) {
        if (isGeOf(value, min)) {
            return value;
        }
        throw new RequireValidateException(message, value, min);
    }

    /**
     * 要求数值和另外一个值相等
     *
     * @param value 待测值
     * @param that  目标值
     *
     * @return value == that
     *
     * @throws RequireValidateException 当 value != that 时
     */
    public final static long requireEqOf(long value, long that) {
        return requireEqOf(value, that, "Invalid value: {}, require: {}.");
    }

    /**
     * 要求数值和另外一个值相等
     *
     * @param value   待测值
     * @param that    目标值
     * @param message 自定义错误消息模板
     *
     * @return value == that
     *
     * @throws RequireValidateException 当 value != that 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static long requireEqOf(long value, long that, String message) {
        if (isEqOf(value, that)) {
            return value;
        }
        throw new RequireValidateException(message, value, that);
    }

    /**
     * 要求数值和另外一个值不相等
     *
     * @param value 待测值
     * @param that  目标值
     *
     * @return value != that
     *
     * @throws RequireValidateException 当 value == that 时
     */
    public final static long requireNotOf(long value, long that) {
        return requireNotOf(value, that, "Invalid value: {}, require not of: {}.");
    }

    /**
     * 要求数值和另外一个值不相等
     *
     * @param value   待测值
     * @param that    目标值
     * @param message 自定义错误消息模板
     *
     * @return value != that
     *
     * @throws RequireValidateException 当 value == that 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static long requireNotOf(long value, long that, String message) {
        if (isNotOf(value, that)) {
            return value;
        }
        throw new RequireValidateException(message, value, that);
    }

    /**
     * 要求小于某个值
     *
     * @param value 待测值
     * @param max   最大值（不含）
     *
     * @return value < max
     *
     * @throws RequireValidateException 当 value >= max 时
     */
    public final static double requireLtOf(double value, double max) {
        return requireLtOf(value, max, "Invalid value: {}, require less than: {}.");
    }

    /**
     * 要求小于某个值
     *
     * @param value   待测值
     * @param max     最大值（不含）
     * @param message 自定义错误消息模板
     *
     * @return value < max
     *
     * @throws RequireValidateException 当 value >= max 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static double requireLtOf(double value, double max, String message) {
        if (isLtOf(value, max)) {
            return value;
        }
        throw new RequireValidateException(message, value, max);
    }

    /**
     * 要求不大于某个值
     *
     * @param value 待测值
     * @param max   最大值（含）
     *
     * @return value <= max
     *
     * @throws RequireValidateException 当 value > max 时
     */
    public final static double requireLeOf(double value, double max) {
        return requireLeOf(value, max, "Invalid value: {}, require not great than: {}.");
    }

    /**
     * 要求不大于某个值
     *
     * @param value   待测值
     * @param max     最大值（含）
     * @param message 自定义错误消息模板
     *
     * @return value <= max
     *
     * @throws RequireValidateException 当 value > max 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static double requireLeOf(double value, double max, String message) {
        if (isLeOf(value, max)) {
            return value;
        }
        throw new RequireValidateException(message, value, max);
    }

    /**
     * 要求大于某个值
     *
     * @param value 待测值
     * @param min   最小值（不含）
     *
     * @return value > min
     *
     * @throws RequireValidateException 当 value <= min 时
     */
    public final static double requireGtOf(double value, double min) {
        return requireGtOf(value, min, "Invalid value: {}, require great than: {}.");
    }

    /**
     * 要求大于某个值
     *
     * @param value   待测值
     * @param min     最小值（不含）
     * @param message 自定义错误消息模板
     *
     * @return value > min
     *
     * @throws RequireValidateException 当 value <= min 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static double requireGtOf(double value, double min, String message) {
        if (isGtOf(value, min)) {
            return value;
        }
        throw new RequireValidateException(message, value, min);
    }

    /**
     * 要求不小于某个值
     *
     * @param value 待测值
     * @param min   最小值（含）
     *
     * @return value >= max
     *
     * @throws RequireValidateException 当 value < min 时
     */
    public final static double requireGeOf(double value, double min) {
        return requireGeOf(value, min, "Invalid value: {}, require not less than: {}.");
    }

    /**
     * 要求不小于某个值
     *
     * @param value   待测值
     * @param min     最小值（含）
     * @param message 自定义错误消息模板
     *
     * @return value >= max
     *
     * @throws RequireValidateException 当 value < min 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static double requireGeOf(double value, double min, String message) {
        if (isGeOf(value, min)) {
            return value;
        }
        throw new RequireValidateException(message, value, min);
    }

    /**
     * 要求数值和另外一个值相等
     *
     * @param value 待测值
     * @param that  目标值
     *
     * @return value == that
     *
     * @throws RequireValidateException 当 value != that 时
     */
    public final static double requireEqOf(double value, double that) {
        return requireEqOf(value, that, "Invalid value: {}, require: {}.");
    }

    /**
     * 要求数值和另外一个值相等
     *
     * @param value   待测值
     * @param that    目标值
     * @param message 自定义错误消息模板
     *
     * @return value == that
     *
     * @throws RequireValidateException 当 value != that 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static double requireEqOf(double value, double that, String message) {
        if (isEqOf(value, that)) {
            return value;
        }
        throw new RequireValidateException(message, value, that);
    }

    /**
     * 要求数值和另外一个值不相等
     *
     * @param value 待测值
     * @param that  目标值
     *
     * @return value != that
     *
     * @throws RequireValidateException 当 value == that 时
     */
    public final static double requireNotOf(double value, double that) {
        return requireNotOf(value, that, "Invalid value: {}, require not of: {}.");
    }

    /**
     * 要求数值和另外一个值不相等
     *
     * @param value   待测值
     * @param that    目标值
     * @param message 自定义错误消息模板
     *
     * @return value != that
     *
     * @throws RequireValidateException 当 value == that 时，可按顺序用“{}”符号接受两个参数：value 和 max
     */
    public final static double requireNotOf(double value, double that, String message) {
        if (isNotOf(value, that)) {
            return value;
        }
        throw new RequireValidateException(message, value, that);
    }

    /**
     * 要求空字符串，即字符串为 null 或长度为 0
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 若字符串为 null 或长度为 0 时返回字符串本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <C extends CharSequence> C requireEmpty(C str) {
        if (isEmpty(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid CharSequence, require an empty string, but got: " + str);
    }

    /**
     * 要求空字符串，即字符串为 null 或长度为 0
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 若字符串为 null 或长度为 0 时返回字符串本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireEmpty(C str, String message) {
        if (isEmpty(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求非空字符串，即字符串至少包含一个有效字符
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 若字符串至少包含一个有效字符时返回字符串本身
     *
     * @throws RequireValidateException 若字符串为 null 或长度为 0 时抛出异常
     */
    public final static <C extends CharSequence> C requireNotEmpty(C str) {
        if (isNotEmpty(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid CharSequence, require a not empty string, but got: " + str);
    }

    /**
     * 要求非空字符串，即字符串至少包含一个有效字符
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串泛型类型
     *
     * @return 若字符串至少包含一个有效字符时返回字符串本身
     *
     * @throws RequireValidateException 若字符串为 null 或长度为 0 时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireNotEmpty(C str, String message) {
        if (isNotEmpty(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求字符串是空白字符串，即 str == null 或 str.length() == 0 或字符串所有字符均是空白字符
     *
     * @param str 待测字符串
     * @param <C> 字符串数据类型
     *
     * @return 当字符串是空白字符串是返回字符串本身
     *
     * @throws RequireValidateException 若字符串为非空白字符串时抛出异常
     */
    public final static <C extends CharSequence> C requireBlank(C str) {
        if (isBlank(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid blank CharSequence, got: " + str);
    }

    /**
     * 要求字符串是空白字符串，即 str == null 或 str.length() == 0 或字符串所有字符均是空白字符
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     字符串数据类型
     *
     * @return 当字符串是空白字符串是返回字符串本身
     *
     * @throws RequireValidateException 若字符串为非空白字符串时抛出异常
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     */
    public final static <C extends CharSequence> C requireBlank(C str, String message) {
        if (isBlank(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求字符串是非空白字符串，至少包含一个非空白字符
     *
     * @param str 待测字符串
     * @param <C> 待测字符串数据类型
     *
     * @return 当符合验证条件时返回字符串本身，否则抛出异常
     *
     * @throws RequireValidateException 若字符串为空白字符串时抛出异常
     * @see #requireHasText(CharSequence)
     */
    public final static <C extends CharSequence> C requireNotBlank(C str) {
        if (isNotBlank(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid CharSequence, require not blank, but got: '" + str + "'");
    }

    /**
     * 要求字符串是非空白字符串，至少包含一个非空白字符
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     待测字符串数据类型
     *
     * @return 当符合验证条件时返回字符串本身，否则抛出异常
     *
     * @throws RequireValidateException 若字符串为空白字符串时抛出异常
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     * @see #requireHasText(CharSequence, String)
     */
    public final static <C extends CharSequence> C requireNotBlank(C str, String message) {
        if (isNotBlank(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }

    /**
     * 要求字符串是非空白字符串，至少包含一个非空白字符
     *
     * @param str 待测字符串
     * @param <C> 待测字符串数据类型
     *
     * @return 当符合验证条件时返回字符串本身，否则抛出异常
     *
     * @throws RequireValidateException 若字符串为空白字符串时抛出异常
     * @see #requireNotBlank(CharSequence)
     */
    public final static <C extends CharSequence> C requireHasText(C str) {
        if (isNotBlank(str)) {
            return str;
        }
        throw new RequireValidateException("Invalid CharSequence, require has text, but got: '" + str + "'");
    }

    /**
     * 要求字符串是非空白字符串，至少包含一个非空白字符
     *
     * @param str     待测字符串
     * @param message 自定义消息模板
     * @param <C>     待测字符串数据类型
     *
     * @return 当符合验证条件时返回字符串本身，否则抛出异常
     *
     * @throws RequireValidateException 若字符串为空白字符串时抛出异常
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参字符串
     * @see #requireNotBlank(CharSequence, String)
     */
    public final static <C extends CharSequence> C requireHasText(C str, String message) {
        if (isNotBlank(str)) {
            return str;
        }
        throw new RequireValidateException(message, str);
    }


    /**
     * 要求空集合，即集合为 null 或长度为 0
     *
     * @param collect 待测集合
     * @param <C>     集合泛型类型
     *
     * @return 若集合为 null 或长度为 0 时返回集合本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常
     */
    public final static <E, C extends Collection<E>> C requireEmpty(C collect) {
        if (isEmpty(collect)) {
            return collect;
        }
        throw new RequireValidateException("Invalid Collection, require an empty Collection.");
    }

    /**
     * 要求空集合，即集合为 null 或长度为 0
     *
     * @param collect 待测集合
     * @param message 自定义消息模板
     * @param <C>     集合泛型类型
     *
     * @return 若集合为 null 或长度为 0 时返回集合本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参集合
     */
    public final static <E, C extends Collection<E>> C requireEmpty(C collect, String message) {
        if (isEmpty(collect)) {
            return collect;
        }
        throw new RequireValidateException(message, collect);
    }

    /**
     * 要求非空集合，即集合中至少有一项数据
     *
     * @param collect 待测集合
     * @param <C>     集合泛型类型
     *
     * @return 若集合中至少有一项数据时返回集合本身
     *
     * @throws RequireValidateException 若集合为 null 或长度为 0 时抛出异常
     */
    public final static <E, C extends Collection<E>> C requireNotEmpty(C collect) {
        if (isNotEmpty(collect)) {
            return collect;
        }
        throw new RequireValidateException("Invalid Collection, require a not empty Collection.");
    }

    /**
     * 要求空集合，即集合中至少有一项数据
     *
     * @param collect 待测集合
     * @param message 自定义消息模板
     * @param <C>     集合泛型类型
     *
     * @return 若集合中至少有一项数据时返回集合本身
     *
     * @throws RequireValidateException 若集合为 null 或长度为 0 时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参集合
     */
    public final static <E, C extends Collection<E>> C requireNotEmpty(C collect, String message) {
        if (isNotEmpty(collect)) {
            return collect;
        }
        throw new RequireValidateException(message, collect);
    }

    /**
     * 要求空 Map，即集合为 null 或长度为 0
     *
     * @param map 待测 Map 映射
     * @param <K> Map 键数据类型
     * @param <V> Map 值数据类型
     * @param <M> Map 数据类型
     *
     * @return 若 Map 为 null 或长度为 0 时返回集合本身
     */
    public final static <K, V, M extends Map<K, V>> M requireEmpty(M map) {
        if (isEmpty(map)) {
            return map;
        }
        throw new RequireValidateException("Invalid Map, require an empty 'Map'.");
    }

    /**
     * 要求空 Map，即集合为 null 或长度为 0
     *
     * @param map     待测 Map 映射
     * @param message 自定义消息模板
     * @param <K>     Map 键数据类型
     * @param <V>     Map 值数据类型
     * @param <M>     Map 数据类型
     *
     * @return 若 Map 为 null 或长度为 0 时返回集合本身
     *
     * @throws RequireValidateException 当检测不通过时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参 Map
     */
    public final static <K, V, M extends Map<K, V>> M requireEmpty(M map, String message) {
        if (isEmpty(map)) {
            return map;
        }
        throw new RequireValidateException(message, map);
    }

    /**
     * 要求非空 Map，即 Map 中至少有一组键值对
     *
     * @param map 待测 Map 映射
     * @param <K> Map 键数据类型
     * @param <V> Map 值数据类型
     * @param <M> Map 数据类型
     *
     * @return 当且仅当 Map 中至少包含一组键值对映射时返回 Map 本身
     *
     * @throws RequireValidateException 当 Map == null 或 Map.isEmpty() 时抛出异常
     */
    public final static <K, V, M extends Map<K, V>> M requireNotEmpty(M map) {
        if (isNotEmpty(map)) {
            return map;
        }
        throw new RequireValidateException("Invalid Map, require a not empty 'Collection'.");
    }

    /**
     * 要求非空 Map，即 Map 中至少有一组键值对
     *
     * @param map     待测 Map 映射
     * @param message 自定义消息模板
     * @param <K>     Map 键数据类型
     * @param <V>     Map 值数据类型
     * @param <M>     Map 数据类型
     *
     * @return 当且仅当 Map 中至少包含一组键值对映射时返回 Map 本身
     *
     * @throws RequireValidateException 当 Map == null 或 Map.isEmpty() 时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参 Map
     */
    public final static <K, V, M extends Map<K, V>> M requireNotEmpty(M map, String message) {
        if (isNotEmpty(map)) {
            return map;
        }
        throw new RequireValidateException(message, map);
    }
}
