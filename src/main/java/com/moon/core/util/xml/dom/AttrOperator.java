package com.moon.core.util.xml.dom;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.OptionalUtil;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.moon.core.util.TypeUtil.cast;

/**
 * @author moonsky
 */
public interface AttrOperator<T extends AttrOperator<T>> {
    /**
     * 返回属性值
     *
     * @param name
     * @return
     */
    String attr(String name);

    /**
     * 返回属性值
     *
     * @param name
     * @return
     */
    default int attrAsInt(String name) { return cast().toIntValue(attr(name)); }

    /**
     * 返回属性值
     *
     * @param name
     * @return
     */
    default long attrAsLong(String name) { return cast().toLongValue(attr(name)); }

    /**
     * 返回属性值
     *
     * @param name
     * @return
     */
    default double attrAsDouble(String name) { return cast().toDoubleValue(attr(name)); }

    /**
     * 返回属性值
     *
     * @param name
     * @return
     */
    default boolean attrAsBoolean(String name) { return cast().toBooleanValue(attr(name)); }

    /**
     * 返回属性值
     *
     * @param name
     * @param defaultValue
     * @return
     */
    default String attrOrDefault(String name, String defaultValue) {
        return StringUtil.defaultIfEmpty(attr(name), defaultValue);
    }

    /**
     * 返回属性值
     *
     * @param name
     * @param getter
     * @return
     */
    default String attrOrElse(String name, Supplier<String> getter) {
        return OptionalUtil.orElseGet(attr(name), getter);
    }

    /**
     * 返回属性值
     *
     * @param name
     * @param defaultValue
     * @return
     */
    default <E> E attrAndMapper(String name, String defaultValue, Function<String, E> mapper) {
        return mapper.apply(attrOrDefault(name, defaultValue));
    }

    /**
     * 设置属性
     *
     * @param name
     * @param value
     * @return
     */
    T attr(String name, Object value);
}
