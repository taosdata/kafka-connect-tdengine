package com.moon.core.util;

import com.moon.core.enums.Const;
import com.moon.core.lang.LongUtil;

import java.util.function.Function;

import static com.moon.core.lang.IntUtil.defaultIfInvalid;
import static com.moon.core.lang.StringUtil.defaultIfWebNull;
import static com.moon.core.lang.StringUtil.trimToNull;

/**
 * @author moonsky
 */
public interface PropertiesSupplier<V> {

    /**
     * override for {@link java.util.Map#get(Object)}
     *
     * @param key
     *
     * @return
     */
    V get(Object key);

    /**
     * get a string value
     *
     * @param key
     *
     * @return
     */
    default String getString(String key) {
        V value = get(key);
        return value == null ? null : value.toString();
    }

    /**
     * Get a string value or an empty string
     *
     * @param key
     *
     * @return
     */
    default String getOrEmpty(String key) { return defaultIfWebNull(getString(key), Const.EMPTY); }

    /**
     * 返回一个字符串或 null
     *
     * @param key
     *
     * @return
     */
    default String getOrNull(String key) { return trimToNull(getString(key)); }

    /**
     * 返回 int 数据
     *
     * @param key
     *
     * @return
     */
    default int getInt(String key) { return Integer.parseInt(getString(key)); }

    /**
     * 返回 int 数据，转换失败返回 0
     *
     * @param key
     *
     * @return
     */
    default int getOrZero(String key) { return getOrDefaultAsInt(key, 0); }

    /**
     * 返回 int 数据，转换失败返回 1
     *
     * @param key
     *
     * @return
     */
    default int getOrOne(String key) { return getOrDefaultAsInt(key, 1); }

    /**
     * 返回 int 数据，转换失败返回 defaultVal
     *
     * @param key
     * @param defaultValue
     *
     * @return
     */
    default int getOrDefaultAsInt(String key, int defaultValue) {
        return defaultIfInvalid(getString(key), defaultValue);
    }

    /**
     * 返回 long 数据
     *
     * @param key
     *
     * @return
     */
    default long getLong(String key) { return Long.parseLong(getString(key)); }

    /**
     * 返回 long 数据，转换失败返回 defaultVal
     *
     * @param key
     * @param defaultValue
     *
     * @return
     */
    default long getOrDefaultAsLong(String key, long defaultValue) {
        return LongUtil.defaultIfInvalid(getString(key), defaultValue);
    }

    /**
     * 返回 boolean 数据
     *
     * @param key
     *
     * @return
     */
    default boolean getBoolean(String key) { return Boolean.valueOf(getString(key)); }

    /**
     * 返回 int 数据，转换失败返回 true
     *
     * @param key
     *
     * @return
     */
    default boolean getOrTrue(String key) { return getOrDefaultAsBoolean(key, true); }

    /**
     * 返回 int 数据，转换失败返回 false
     *
     * @param key
     *
     * @return
     */
    default boolean getOrFalse(String key) { return getBoolean(key); }

    /**
     * 返回 int 数据，转换失败返回 defaultVal
     *
     * @param key
     * @param defaultVal
     *
     * @return
     */
    default boolean getOrDefaultAsBoolean(String key, boolean defaultVal) {
        String value = getString(key), trueVal = "true", falseVal = "false";
        if (trueVal.equalsIgnoreCase(value)) {
            return true;
        }
        if (falseVal.equalsIgnoreCase(value)) {
            return false;
        }
        return defaultVal;
    }

    /**
     * 自定义返回值转换成指定类型数据
     *
     * @param key
     * @param transformer
     * @param <T>
     *
     * @return
     */
    default <T> T getAndTransform(String key, Function<String, T> transformer) {
        return transformer.apply(getString(key));
    }
}
