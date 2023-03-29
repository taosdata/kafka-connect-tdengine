package com.moon.core.model;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public interface BaseSupporter {

    /**
     * null or toString
     *
     * @param data
     *
     * @return
     */
    default String stringify(Object data) { return data == null ? null : data.toString(); }

    /**
     * 转换，确保不会返回空对象
     *
     * @param set       源集合
     * @param mapper    转换器
     * @param container 转换后的容器
     * @param <T>       源类型
     * @param <E>       目标类型
     * @param <C>       集合容器类型
     *
     * @return
     */
    default <T, E, C extends Collection<E>> C mapOrEmpty(
        Collection<T> set, Function<? super T, ? extends E> mapper, Supplier<? extends C> container
    ) {
        C resultSet = container.get();
        for (T item : set) { resultSet.add(mapper.apply(item)); }
        return resultSet;
    }

    /**
     * 转换成 HashSet
     *
     * @param set    源集合
     * @param mapper 转换器
     * @param <T>    源类型
     * @param <E>    目标类型
     *
     * @return 转换后的 HashSet
     */
    default <T, E> HashSet<E> mapAsSetOrEmpty(
        Collection<T> set, Function<? super T, ? extends E> mapper
    ) { return mapOrEmpty(set, mapper, HashSet::new); }

    /**
     * 转换
     *
     * @param set
     * @param mapper
     * @param <T>
     * @param <E>
     *
     * @return
     */
    default <T, E> ArrayList<E> mapAsListOrEmpty(
        Collection<T> set, Function<? super T, ? extends E> mapper
    ) { return mapOrEmpty(set, mapper, ArrayList::new); }

    /**
     * 默认
     *
     * @param value
     * @param defaultValue
     * @param <T>
     *
     * @return
     */
    default <T> T defaultIfNull(T value, T defaultValue) { return value == null ? defaultValue : value; }

    /**
     * 默认
     *
     * @param value
     * @param supplier
     * @param <T>
     *
     * @return
     */
    default <T> T elseIfNull(T value, Supplier<T> supplier) { return value == null ? supplier.get() : value; }

    /**
     * property value or null
     *
     * @param e
     * @param presentGetter
     * @param <E>
     * @param <V>
     *
     * @return
     */
    default <E, V> V obtainIfNonNull(E e, Function<? super E, ? extends V> presentGetter) {
        return e == null ? null : presentGetter.apply(e);
    }

    /**
     * 设置 hash set
     *
     * @param set
     * @param setter
     * @param <T>
     *
     * @return
     */
    default <T> Set<T> ensureHashSet(Set set, Consumer<? super Set> setter) {
        return ensureSet(set, setter, HashSet::new);
    }

    /**
     * 设置 array list
     *
     * @param list
     * @param setter
     * @param <T>
     *
     * @return
     */
    default <T> List<T> ensureArrayList(List<T> list, Consumer<? super List> setter) {
        return ensureList(list, setter, ArrayList::new);
    }

    /**
     * 设置 set
     *
     * @param set
     * @param setter
     * @param setSupplier
     * @param <T>
     *
     * @return
     */
    default <T, S extends Set<T>> S ensureSet(
        S set, Consumer<? super S> setter, Supplier<? extends S> setSupplier
    ) { return ensureNonNull(set, setter, setSupplier); }

    /**
     * 设置 list
     *
     * @param list
     * @param setter
     * @param listSupplier
     * @param <T>
     *
     * @return
     */
    default <T, L extends List<T>> L ensureList(
        L list, Consumer<? super L> setter, Supplier<? extends L> listSupplier
    ) { return ensureNonNull(list, setter, listSupplier); }

    /**
     * 设置 object
     *
     * @param t
     * @param setter
     * @param elseIfAbsent
     * @param <T>
     *
     * @return
     */
    default <T> T ensureNonNull(
        T t, Consumer<? super T> setter, Supplier<? extends T> elseIfAbsent
    ) {
        if (t == null) { setter.accept(t = elseIfAbsent.get()); }
        return t;
    }
}
