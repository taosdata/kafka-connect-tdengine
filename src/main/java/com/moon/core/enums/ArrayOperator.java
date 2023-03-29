package com.moon.core.enums;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.function.BiIntConsumer;
import com.moon.core.util.interfaces.IteratorFunction;
import com.moon.core.util.interfaces.Stringify;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
public interface ArrayOperator extends IteratorFunction<Object, Object>, Predicate, Stringify, IntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     *
     * @return the function result
     */
    @Override
    Object apply(int value);

    /**
     * 获取空数组对象
     *
     * @param <T> 数组元素泛型类型
     *
     * @return 空数组
     */
    default <T> T empty() { return null; }

    /**
     * 创建一个指定长度数组
     *
     * @param length 数组长度
     * @param <T>    数组元素泛型类型
     *
     * @return 指定长度数组
     */
    default <T> T create(int length) {return (T) apply(length);}

    /**
     * 字符串化对象
     *
     * @param o 目标数组
     *
     * @return 数组字符串
     */
    @Override
    default String stringify(Object o) {return ArrayUtil.stringify(o);}

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param o the input argument
     *
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    default boolean test(Object o) { return o != null && o.getClass().isArray(); }

    /**
     * 获取一个迭代器
     *
     * @param o 数组
     *
     * @return 数组迭代器，不可执行{@link Iterator#remove()}方法
     */
    @Override
    default Iterator<Object> iterator(Object o) { return IteratorUtil.ofAny(o); }

    /**
     * 包装类型数组转换为基本数据类型数组
     *
     * @param arr 包装数据类型数组
     * @param <T> 基本数据类型
     *
     * @return 基本数据类型数组
     */
    default <T> T toPrimitives(Object arr) { return (T) arr; }

    /**
     * 基本数据类型数组转换为包装类型数组
     *
     * @param arr 基本数据类型数组
     * @param <T> 包装数据类型
     *
     * @return 包装数据类型数组
     */
    default <T> T toObjects(Object arr) { return (T) arr; }

    /**
     * 默认空数组
     *
     * @param arr 数组
     * @param <T> 泛型类型
     *
     * @return 空数组或 null
     */
    default <T> T emptyIfNull(Object arr) { return defaultIfNull(arr, empty()); }

    /**
     * 指定默认值
     *
     * @param arr           数组
     * @param defaultIfNull 默认值
     * @param <T>           泛型类型
     *
     * @return 返回值
     */
    default <T> T defaultIfNull(Object arr, T defaultIfNull) { return arr == null ? defaultIfNull : (T) arr; }

    /**
     * 指定默认值
     *
     * @param arr            数组
     * @param defaultIfEmpty 默认值
     * @param <T>            泛型类型
     *
     * @return 返回值
     */
    default <T> T defaultIfEmpty(Object arr, T defaultIfEmpty) { return isEmpty(arr) ? defaultIfEmpty : (T) arr; }

    /**
     * 或者数组指定索引项
     *
     * @param arr   数组
     * @param index 索引
     *
     * @return 指定索引的值
     */
    default <T> T get(Object arr, int index) { return (T) Array.get(arr, index); }

    /**
     * 设置值
     *
     * @param arr   数组
     * @param index 指定索引
     * @param value 值
     *
     * @return 数组
     */
    default Object set(Object arr, int index, Object value) {
        Object old = Array.get(arr, index);
        Array.set(arr, index, value);
        return old;
    }

    /**
     * 求数组长度
     *
     * @param arr 数组
     *
     * @return 数组长度
     */
    default int length(Object arr) { return arr == null ? 0 : Array.getLength(arr); }

    /**
     * 迭代处理数组每一项
     *
     * @param arr      数组
     * @param consumer 数组依次执行的函数
     */
    default void forEach(Object arr, BiIntConsumer consumer) {
        for (int i = 0, len = length(arr); i < len; i++) {
            consumer.accept(get(arr, i), i);
        }
    }

    /**
     * 数组是否包含某一项
     *
     * @param arr  目标数组
     * @param item 目标项
     *
     * @return 数组是否包含某一项
     */
    default boolean contains(Object arr, Object item) {
        if (arr == null) {
            return false;
        }
        int len = length(arr);
        if (len == 0) {
            return false;
        }
        if (item == null) {
            for (int i = 0; i < len; i++) {
                if (get(arr, i) == null) {
                    return true;
                }
            }
        }
        for (int i = 0; i < len; i++) {
            if (Objects.equals(item, get(arr, i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组是否为空
     *
     * @param arr 目标数组
     *
     * @return 目标数组是否是空数组
     */
    default boolean isEmpty(Object arr) { return length(arr) == 0; }
}
