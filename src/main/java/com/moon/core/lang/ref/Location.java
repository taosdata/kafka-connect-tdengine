package com.moon.core.lang.ref;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * 推荐使用{@link Table}、{@link TableImpl}
 *
 * @author moonsky
 */
@Deprecated
public interface Location<X, Y, Z> {

    /**
     * 设置一个值
     *
     * @param x namespace
     * @param y key
     * @param z value
     *
     * @return this
     */
    Location<X, Y, Z> put(X x, Y y, Z z);

    /**
     * 放置所有
     *
     * @param x   namespace
     * @param map values entry
     *
     * @return this
     */
    Location<X, Y, Z> putAll(X x, Map<? extends Y, ? extends Z> map);

    /**
     * 获取一个值
     *
     * @param x namespace
     * @param y key
     *
     * @return this
     */
    Z get(X x, Y y);

    /**
     * 清空
     *
     * @return this
     */
    Location<X, Y, Z> clear();

    /**
     * 清空
     *
     * @param x namespace
     *
     * @return this
     */
    Location<X, Y, Z> clear(X x);

    /**
     * 获取值，或返回默认值
     *
     * @param x            namespace
     * @param y            key
     * @param defaultValue defaultValue
     *
     * @return this
     */
    default Z getOrDefault(X x, Y y, Z defaultValue) {
        Z value = get(x, y);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取值，或返回默认值
     *
     * @param x        namespace
     * @param y        key
     * @param supplier 默认值 getter
     *
     * @return value
     */
    default Z getOrElse(X x, Y y, Supplier<Z> supplier) {
        Z value = get(x, y);
        return value == null ? supplier.get() : value;
    }

    /**
     * 获取值，或返回默认值
     *
     * @return value
     */
    default Z getOrWithDefault(X x, Y y, Z defaultValue) {
        Z value = get(x, y);
        if (value == null) { put(x, y, value = defaultValue); }
        return value;
    }

    /**
     * 获取值，或返回执行结果
     *
     * @param x        namespace
     * @param y        key
     * @param supplier 默认值
     *
     * @return value
     */
    default Z getOrWithElse(X x, Y y, Supplier<Z> supplier) {
        Z value = get(x, y);
        if (value == null) { put(x, y, value = supplier.get()); }
        return value;
    }

    /**
     * 获取值，或返回执行结果
     *
     * @param x        namespace
     * @param y        key
     * @param computer computer
     *
     * @return value
     */
    default Z getOrWithCompute(X x, Y y, BiFunction<X, Y, Z> computer) {
        Z value = get(x, y);
        if (value == null) { put(x, y, value = computer.apply(x, y)); }
        return value;
    }
}
