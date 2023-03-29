package com.moon.core.util.validator;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.RequireValidateException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;

/**
 * @author moonsky
 */
abstract class BaseValidator<T, IMPL extends BaseValidator<T, IMPL>> extends Value<T>
    implements Cloneable, Serializable, IValidator<T, IMPL>, Supplier<T> {

    static final Function<String, ? extends RuntimeException> EXCEPTION_BUILDER = RequireValidateException::new;

    static final long serialVersionUID = 1L;

    static final String SEPARATOR = "; ";

    private Function<String, ? extends RuntimeException> exceptionBuilder;

    private List<String> messages;

    private boolean immediate;

    private String separator;

    BaseValidator(T value, boolean nullable, List<String> messages, String separator, boolean immediate) {
        super(value, nullable);
        this.separator = separator;
        this.immediate = immediate;
        this.messages = messages;
    }

    /*
     * -----------------------------------------------------------------
     * format messages
     * -----------------------------------------------------------------
     */

    final static String KEY = "{key}", VALUE = "{value}", PLACEHOLDER = "{}", DIGIT = "%d", COUNT = "{count}";

    final static String requireCountOf = "Require count of: {count}.";
    final static String requireAtLeast = "Require count at least of: {count}.";
    final static String requireAtMost = "Require count at most of: {count}.";
    final static String requireOnly = "Require unique items.";
    final static String requireNone = "Require none items.";
    final static String requireEvery = "Require every item matches.";
    final static String invalidValue = "Invalid value: {}.";
    final static String requirePresentKey = "Require present key of : {}";

    static <T> T dftIfNull(T obj, T defaultValue) { return obj == null ? defaultValue : obj; }

    private static String doFormatValue(String template, Object value) {
        String message = StringUtil.replaceAll(template, VALUE, value);
        return StringUtil.replaceFirst(message, PLACEHOLDER, value);
    }

    private static String doFormatCount(String template, int count) {
        String message = StringUtil.replaceAll(template, DIGIT, count);
        return StringUtil.replaceAll(message, COUNT, count);
    }

    private static String doFormatEntry(String template, Object key, Object value) {
        return StringUtil.replaceAll(doFormatValue(template, value), KEY, key);
    }

    private static String doFormatEntryCount(String template, Object key, Object value, int count) {
        return doFormatEntry(doFormatCount(template, count), key, value);
    }

    final static String transform4Count(String template, int count) {
        template = dftIfNull(template, "Invalid count: {}");
        return doFormatCount(template, count);
    }

    final static String transform4Value(String template, Object value) {
        template = dftIfNull(template, "Invalid value: {}");
        String message = StringUtil.replaceAll(template, VALUE, value);
        return StringUtil.replaceFirst(message, PLACEHOLDER, value);
    }

    final static String transform4Value(String template, Object value, int count) {
        template = dftIfNull(template, "Invalid value: {}");
        return doFormatValue(doFormatCount(template, count), value);
    }

    final static String transform4Entry(String template, Object key, Object value) {
        template = dftIfNull(template, "Invalid entry: {key: '{key}', value: '{value}'}");
        return doFormatEntry(template, key, value);
    }

    final static String transform4Entry(String template, Object key, Object value, int count) {
        template = dftIfNull(template, "Invalid entry: {key: '{key}', value: '{value}'}");
        return doFormatEntryCount(template, key, value, count);
    }

    final static String transform4Indexed(String template, Object index, Object value) {
        template = dftIfNull(template, "Invalid data: {index: '{key}', value: '{value}'}");
        return doFormatEntry(template, index, value);
    }

    final static String transform4Indexed(String template, Object index, Object value, int count) {
        template = dftIfNull(template, "Invalid data: {index: '{key}', value: '{value}'}");
        return doFormatEntryCount(template, index, value, count);
    }

    /*
     * -----------------------------------------------------------------
     * create messages
     * -----------------------------------------------------------------
     */

    /**
     * 添加一条错误信息，如果设置了立即结束将抛出异常
     *
     * @param message 消息内容
     *
     * @return this
     */
    final IMPL createMsg(String message) {
        if (immediate) {
            throw getExceptionBuilder().apply(message);
        } else {
            ensureMessages().add(message);
        }
        return current();
    }

    final IMPL createMsg(String template, Object value) {
        return createMsg(transform4Value(template, value));
    }

    final IMPL createMsgAtMost(String message, int count) {
        return createMsg(transform4Count(dftIfNull(message, "最多只能有 %d 项符合条件"), count));
    }

    final IMPL createMsgAtLeast(String message, int count) {
        return createMsg(transform4Count(dftIfNull(message, "至少需要有 %d 项符合条件"), count));
    }

    final IMPL createMsgCountOf(String message, int count) {
        return createMsg(transform4Count(dftIfNull(message, "只能有 %d 项符合条件"), count));
    }

    final <V> IMPL useEffective(boolean matched, V value, String message) {
        return matched ? current() : createMsg(message, value);
    }

    final <V> IMPL useEffective(V value, Predicate<? super V> tester, String message) {
        return useEffective(tester.test(value), value, message);
    }

    /*
     * -----------------------------------------------------------------
     * inner member methods
     * -----------------------------------------------------------------
     */

    /**
     * 确保存在错误小消息集合，并返回
     *
     * @return 错误消息集合
     */
    final List<String> ensureMessages() { return messages == null ? (messages = new ArrayList<>()) : messages; }

    /**
     * 当前对象
     *
     * @return 当前对象
     */
    final IMPL current() { return (IMPL) this; }

    /*
     * -----------------------------------------------------------------
     * inner static methods
     * -----------------------------------------------------------------
     */

    final static <IMPL extends BaseValidator<M, IMPL>, M extends Map<K, V>, K, V> IMPL requireAtMostCountOf(
        IMPL impl, BiPredicate<? super K, ? super V> tester, int count, String message
    ) {
        final M value = impl.getValue();
        int amount = 0;
        for (Map.Entry<K, V> item : value.entrySet()) {
            if (tester.test(item.getKey(), item.getValue()) && (++amount > count)) {
                return impl.createMsgAtMost(message, count);
            }
        }
        return amount > count ? impl.createMsgAtMost(message, count) : impl;
    }

    final static <IMPL extends BaseValidator<M, IMPL>, M extends Map<K, V>, K, V> IMPL requireAtLeastCountOf(
        IMPL impl, BiPredicate<? super K, ? super V> tester, int count, String message
    ) {
        final M value = impl.getValue();
        int amount = 0;
        for (Map.Entry<K, V> item : value.entrySet()) {
            if (tester.test(item.getKey(), item.getValue()) && (++amount >= count)) {
                return impl;
            }
        }
        return amount < count ? impl.createMsgAtLeast(message, count) : impl;
    }

    final static <IMPL extends BaseValidator<M, IMPL>, M extends Map<K, V>, K, V> IMPL requireCountOf(
        IMPL impl, BiPredicate<? super K, ? super V> tester, int count, String message
    ) {
        final M value = impl.getValue();
        int amount = 0;
        for (Map.Entry<K, V> item : value.entrySet()) {
            if (tester.test(item.getKey(), item.getValue()) && (++amount > count)) {
                return impl.createMsgCountOf(message, count);
            }
        }
        return amount < count ? impl.createMsgCountOf(message, count) : impl;
    }

    /*
     * -----------------------------------------------------------------
     * public methods
     * -----------------------------------------------------------------
     */

    /**
     * 异常构造器
     *
     * @param exceptionBuilder 异常构造器
     *
     * @return 当前对象
     */
    public IMPL setExceptionBuilder(Function<String, ? extends RuntimeException> exceptionBuilder) {
        this.exceptionBuilder = exceptionBuilder;
        return current();
    }

    public final Function<String, ? extends RuntimeException> getExceptionBuilder() {
        return exceptionBuilder == null ? EXCEPTION_BUILDER : exceptionBuilder;
    }

    /**
     * 错误消息集合
     *
     * @return 所有错误消息集合
     */
    public final List<String> getMessages() { return new ArrayList<>(ensureMessages()); }

    /**
     * 是否错误即时结束
     *
     * @return true: 立即结束； false: 最后统一结束
     */
    public final boolean isImmediate() { return immediate; }

    /**
     * 分隔符
     *
     * @return 分隔符
     */
    public final String getSeparator() { return separator; }

    /**
     * 获取错误信息，用默认分隔符
     *
     * @return 错误消息内容
     */
    public final String getMessage() { return getMessage(StringUtil.defaultIfNull(separator, SEPARATOR)); }

    /**
     * 所有错误消息组成的字符串，自定义分隔符
     *
     * @param separator 分隔符
     *
     * @return 错误消息内容
     */
    public final String getMessage(String separator) {
        return JoinerUtil.of(separator).skipNulls().join(ensureMessages()).toString();
    }

    @Override
    public final String toString() { return getMessage(); }

    @Override
    public final int hashCode() { return Objects.hashCode(value); }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        return Objects.equals(value, ((Value) obj).value);
    }

    /*
     * -----------------------------------------------------------------
     * get value or source
     * -----------------------------------------------------------------
     */

    /**
     * 验证通过，返回该对象，否则返回 null
     *
     * @return 数据对象，当符合验证时，返回源对象；否则返回 null
     */
    public final T nullIfInvalid() { return defaultIfInvalid(null); }

    /**
     * 验证通过，返回该对象，否则返回指定的默认值
     *
     * @param elseValue 不符合验证规则的默认值
     *
     * @return 数据对象，当符合验证时，返回源对象；否则返回自定义提供的对象
     */
    public final T defaultIfInvalid(T elseValue) { return isValid() ? value : elseValue; }

    /**
     * 验证通过，返回该对象，否则返回指定的默认值
     *
     * @param elseGetter 不符合验证规则的默认值
     *
     * @return 数据对象，当符合验证时，返回源对象；否则返回自定义提供的对象
     */
    public final T elseIfInvalid(Supplier<T> elseGetter) { return isValid() ? value : elseGetter.get(); }

    /**
     * 验证通过，返回该对象，否则抛出异常
     *
     * @return 符合验证规则的原对象
     *
     * @throws RequireValidateException 如果验证不通过，抛出这个异常，异常内容为所有验证不通过规则的内容
     */
    @Override
    public final T get() {
        if (isValid()) {
            return value;
        }
        throw getExceptionBuilder().apply(getMessage());
    }

    /**
     * 验证通过，返回该对象，否则抛出指定信息异常
     *
     * @param errorMessage 指定错误消息
     *
     * @return 符合验证规则的原对象
     *
     * @throws RequireValidateException 如果验证不通过，抛出异常
     */
    public final T get(String errorMessage) {
        if (isValid()) {
            return value;
        }
        throw getExceptionBuilder().apply(errorMessage);
    }

    /**
     * 验证通过，返回该对象，否则抛出指定异常
     *
     * @param exceptionBuilder 异常构建器
     * @param <EX>             异常类型
     *
     * @return 符合验证的目标对象
     *
     * @throws EX 异常
     */
    public final <EX extends Throwable> T get(Function<String, EX> exceptionBuilder) throws EX {
        if (isValid()) {
            return value;
        }
        throw exceptionBuilder.apply(toString());
    }

    /*
     * -----------------------------------------------------------------
     * defaults and presets
     * -----------------------------------------------------------------
     */

    /**
     * 是否验证通过
     *
     * @return true：验证通过；false：验证不通过
     */
    public final boolean isValid() { return messages == null || messages.isEmpty(); }

    /**
     * 是否验证不通过
     *
     * @return true：验证不通过；false：验证通过
     */
    public final boolean isInvalid() { return !isValid(); }

    /**
     * 当验证通过时执行处理
     *
     * @param consumer 处理器
     *
     * @return 当前 Validator 对象
     */
    public IMPL ifValid(Consumer<? super T> consumer) {
        if (isValid()) {
            consumer.accept(value);
        }
        return current();
    }

    /**
     * 当验证不通过时执行处理
     *
     * @param consumer 处理器吗，接受一个参数: 源对象
     *
     * @return 当前 Validator 对象
     */
    public IMPL ifInvalid(Consumer<? super T> consumer) {
        if (isInvalid()) {
            consumer.accept(value);
        }
        return current();
    }

    /**
     * 当验证不通过时执行处理
     *
     * @param consumer 处理器，接受两个参数: 源对象，错误消息
     *
     * @return 当前 Validator 对象
     */
    public IMPL ifInvalid(BiConsumer<? super T, List<String>> consumer) {
        if (isInvalid()) {
            consumer.accept(value, getMessages());
        }
        return current();
    }

    /**
     * 直接添加一条错误消息
     *
     * @param message 消息内容
     *
     * @return 当前 Validator 实例
     */
    @Override
    public IMPL addErrorMessage(String message) { return createMsg(message); }

    /**
     * 可用于在后面条件验证前预先设置一部分默认值
     * <p>
     * 只能改变{@link #value}内部属性的值，不能重新设置 value
     *
     * @param consumer 预处理器
     *
     * @return 当前 Validator 实例
     */
    public IMPL preset(Consumer<? super T> consumer) {
        consumer.accept(value);
        return current();
    }

    /**
     * 设置是否立即终止，如果设置了即时终止，那么在设置之后第一个验证不通过会立即抛出异常
     * <p>
     * 否则在最后才抛出异常
     *
     * @param immediate 是否即时终止
     *
     * @return 当前 Validator 实例
     */
    public IMPL setImmediate(boolean immediate) {
        this.immediate = immediate;
        return current();
    }

    /**
     * 设置错误信息分隔符，不能为 null
     *
     * @param separator 错误消息分割符
     *
     * @return 当前 Validator 实例
     */
    public IMPL setSeparator(String separator) {
        this.separator = separator;
        return current();
    }

    /**
     * 条件验证
     * <p>
     * 在前置条件匹配的情况下会执行 when 和 end 之间的验证或其他逻辑
     *
     * @param tester          进入条件验证的函数
     * @param scopedValidator 符合验证条件下的验证函数
     *
     * @return 当前 IValidator 对象
     */
    @Override
    public IMPL when(Predicate<? super T> tester, Consumer<IMPL> scopedValidator) {
        if (tester.test(getValue())) {
            scopedValidator.accept(current());
        }
        return current();
    }

    /**
     * 仅在{@link #getValue()}值不等于{@code null}时执行验证
     *
     * @param scopedValidator 验证函数
     *
     * @return 当前 IValidator 对象
     */
    protected IMPL ifNotNull(Consumer<IMPL> scopedValidator) {
        return when(v -> v != null, scopedValidator);
    }

    /*
     * -----------------------------------------------------------------
     * requires
     * -----------------------------------------------------------------
     */

    /**
     * 要求符合条件
     *
     * @param tester  断言函数
     * @param message 错误消息
     *
     * @return 当前 Validator 对象
     */
    @Override
    public IMPL require(Predicate<? super T> tester, String message) {
        return useEffective(getValue(), tester, message);
    }
}
