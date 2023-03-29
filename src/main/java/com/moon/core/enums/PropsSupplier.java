package com.moon.core.enums;

import com.moon.core.lang.StringUtil;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
interface PropsSupplier extends EnumDescriptor, Supplier<String> {
    /**
     * Gets a key
     *
     * @return
     */
    String key();

    /**
     * Gets a value
     *
     * @return
     */
    String value();

    /**
     * Gets a value
     *
     * @return
     */
    @Override
    default String get() { return value(); }

    /**
     * Gets a value, if value is an empty string,
     * will return a default value
     *
     * @param defaultValue
     * @return
     */
    default String getOrDefault(String defaultValue) { return StringUtil.defaultIfEmpty(get(), defaultValue); }

    /**
     * Gets a value, if value is an empty string,
     * will return a null value
     *
     * @return
     */
    default String getOrNull() { return getOrDefault(null); }

    /**
     * 如果某个参数配置了值，就执行函数
     *
     * @param consumer
     */
    default void ifConfigured(Consumer<String> consumer) {
        String val = getOrNull();
        if (val != null) {
            consumer.accept(val);
        }
    }

    /**
     * 如果某个参数配置了值，就执行函数，并返回；
     * 否则返回默认值
     *
     * @param fn
     * @param defaultVal
     * @param <T>
     * @return
     */
    default <T> T ifConfiguredOrDefault(Function<String, ? extends T> fn, T defaultVal) {
        String val = getOrNull();
        return val == null ? defaultVal : fn.apply(val);
    }

    /**
     * 如果某个参数配置了值，就执行函数，并返回；
     * 否则返回默认提供的数据
     *
     * @param fn
     * @param supplier
     * @param <T>
     * @return
     */
    default <T> T ifConfiguredOrElse(Function<String, ? extends T> fn, Supplier<? extends T> supplier) {
        String val = getOrNull();
        return val == null ? supplier.get() : fn.apply(val);
    }
}
