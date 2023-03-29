package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class SetUtil extends CollectUtil {

    private SetUtil() { ThrowUtil.noInstanceError(); }

    public static Set empty() {return Collections.EMPTY_SET;}

    /*
     * ---------------------------------------------------------------------------------
     * of hash set
     * ---------------------------------------------------------------------------------
     */

    public static <T> HashSet<T> newSet() { return new HashSet<>(); }

    public static <T> HashSet<T> newSet(int initCapacity) { return new HashSet<>(initCapacity); }

    public static <T> HashSet<T> newSet(T... values) { return addAll(newSet(values.length), values); }

    public static <T> HashSet<T> newSet(Iterable<T> iterable) {
        return iterable == null ? newSet() : (iterable instanceof Collection ? new HashSet((Collection) iterable) : addAll(
            newSet(),
            iterable));
    }

    public static <T> HashSet<T> newSet(Iterator<T> iterator) { return addAll(newSet(), iterator); }

    public static <T> HashSet<T> newHashSet() { return new HashSet<>(); }

    public static <T> HashSet<T> newHashSet(int initCapacity) { return new HashSet<>(initCapacity); }

    public static <T> HashSet<T> newHashSet(T value) { return add(newHashSet(), value); }

    public static <T> HashSet<T> newHashSet(T value1, T value2) { return add(newHashSet(value1), value2); }

    public static <T> HashSet<T> newHashSet(T value1, T value2, T value3) {
        return add(newHashSet(value1, value2), value3);
    }

    public static <T> HashSet<T> newHashSet(T... values) { return addAll(newHashSet(values.length), values); }

    public static <T> HashSet<T> newHashSet(Collection<T> collect) {
        return collect == null ? newHashSet() : new HashSet<>(collect);
    }

    public static <T> HashSet<T> newHashSet(Iterable<T> iterable) {
        return iterable == null ? newHashSet() : (iterable instanceof Collection ? new HashSet((Collection) iterable) : addAll(
            newHashSet(),
            iterable));
    }

    public static <T> HashSet<T> newHashSet(Iterator<T> iterator) { return addAll(newHashSet(), iterator); }

    /*
     * ---------------------------------------------------------------------------------
     * of linked hash set
     * ---------------------------------------------------------------------------------
     */

    public static <T> LinkedHashSet<T> newLinkedHashSet() { return new LinkedHashSet<>(); }

    public static <T> LinkedHashSet<T> newLinkedHashSet(int initCapacity) { return new LinkedHashSet<>(initCapacity); }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T value) { return add(newLinkedHashSet(), value); }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T value1, T value2) {
        return add(newLinkedHashSet(value1), value2);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T value1, T value2, T value3) {
        return add(newLinkedHashSet(value1, value2), value3);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T... values) {
        return addAll(newLinkedHashSet(values.length), values);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(Collection<T> collect) {
        return collect == null ? newLinkedHashSet() : new LinkedHashSet<>(collect);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(Iterable<T> iterable) {
        return iterable == null ? newLinkedHashSet() : (iterable instanceof Collection ? new LinkedHashSet((Collection) iterable) : addAll(
            newLinkedHashSet(),
            iterable));
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(Iterator<T> iterator) {
        return addAll(newLinkedHashSet(), iterator);
    }

    /*
     * ---------------------------------------------------------------------------------
     * of tree set
     * ---------------------------------------------------------------------------------
     */

    public static <T> TreeSet<T> newTreeSet() { return new TreeSet<>(); }

    public static <T> TreeSet<T> newTreeSet(T value) { return add(newTreeSet(), value); }

    public static <T> TreeSet<T> newTreeSet(T value1, T value2) { return add(newTreeSet(value1), value2); }

    public static <T> TreeSet<T> newTreeSet(T value1, T value2, T value3) {
        return add(newTreeSet(value1, value2), value3);
    }

    public static <T> TreeSet<T> newTreeSet(T... values) { return addAll(newTreeSet(), values); }

    public static <T> TreeSet<T> newTreeSet(Collection<T> collect) {
        return collect == null ? newTreeSet() : new TreeSet<>(collect);
    }

    public static <T> TreeSet<T> newTreeSet(Iterable<T> iterable) {
        return iterable == null ? newTreeSet() : (iterable instanceof Collection ? new TreeSet((Collection) iterable) : addAll(
            newTreeSet(),
            iterable));
    }

    public static <T> TreeSet<T> newTreeSet(SortedSet<T> sortedSet) { return new TreeSet<>(sortedSet); }

    public static <T> TreeSet<T> newTreeSet(Comparator<? super T> comparator) { return new TreeSet<>(comparator); }

    public static <T> TreeSet<T> newTreeSet(Comparator<? super T> comparator, Iterable<T> iterable) {
        return addAll(newTreeSet(comparator), iterable);
    }

    public static <T> TreeSet<T> newTreeSet(Comparator<? super T> comparator, T... values) {
        return addAll(newTreeSet(comparator), values);
    }

    public static <T> TreeSet<T> newTreeSet(Iterator<T> iterator) { return addAll(newTreeSet(), iterator); }

    public static <S, T> Set<T> mapAsSet(Collection<S> src, Function<? super S, T> mapper) {
        Collection<T> collect = map(src, mapper);
        return collect instanceof Set ? (Set<T>) collect : newSet(collect);
    }

    /*
     * ---------------------------------------------------------------------------------
     * keepers
     * ---------------------------------------------------------------------------------
     */

    /**
     * 如果集合是空集合（null 或 size() == 0）这返回 null
     *
     * @param set set
     * @param <T> set 元素类型
     *
     * @return null set if is an empty set or null
     */
    public static <T> Set<T> nullIfEmpty(Set<T> set) { return isEmpty(set) ? null : set; }

    /**
     * 如果 valuesSet 是 null 则创建一个新的 ArrayList 返回
     *
     * @param set set
     * @param <T> set 元素类型
     *
     * @return empty set if null
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) { return isEmpty(set) ? empty() : set; }

    /**
     * 确保返回集合不为 null
     *
     * @param set set
     * @param <T> set 元素类型
     *
     * @return empty Set if null
     */
    public static <T> Set<T> newIfNull(Set<T> set) { return newIfNull(set, SetUtil::newSet); }

    /**
     * 通常用于确保返回集合不为 null
     *
     * @param set     set
     * @param creator set 构建器
     * @param <T>     set 元素类型
     *
     * @return empty Set if null
     */
    public static <T> Set<T> newIfNull(
        Set<T> set, Supplier<? extends Set<T>> creator
    ) { return set == null ? creator.get() : set; }

    /**
     * 确保返回集合是可操作的{@code set}，用于规避{@link Collections#emptySet()}
     *
     * @param set
     * @param <T>
     *
     * @return
     */
    public static <T> Set<T> newIfEmpty(Set<T> set) { return newIfEmpty(set, SetUtil::newSet); }

    /**
     * 通常用于确保返回集合可操作的{@code set}
     *
     * @param set     set
     * @param creator set 构建器
     * @param <T>     set 元素类型
     *
     * @return empty Set if null
     */
    public static <T> Set<T> newIfEmpty(Set<T> set, Supplier<? extends Set<T>> creator) {
        return isEmpty(set) ? creator.get() : set;
    }

    /**
     * 连接多个集合，返回新集合
     *
     * @param set  基础集合
     * @param sets 待连接集合
     * @param <T>  集合数据类型
     *
     * @return 连接后的集合
     */
    public static <T> Set<T> concat(Set<T> set, Set<T>... sets) { return (Set) concat0(set, sets); }

    public static <T> T requireGet(Set<T> set, int index) { return get(set, index); }

    public static <T> T nullableGet(Set<T> set, int index) {
        int size = set.size(), idx = 0;
        if (size <= index || index < 0) {
            return null;
        }
        for (T item : set) {
            if (idx++ == index) {
                return item;
            }
        }
        return null;
    }

    public static <T> T get(Set<T> set, int index) {
        int size = set.size(), idx = 0;
        if (size <= index || index < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        for (T item : set) {
            if (idx++ == index) {
                return item;
            }
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public static <T> T getByObject(Object set, int index) { return get((Set<T>) set, index); }
}
