package com.moon.core.util;

import java.util.Comparator;
import java.util.function.Function;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ComparatorUtil {

    private ComparatorUtil() { noInstanceError(); }

    /**
     * 多条件比较器
     *
     * @param comparators 自定义比较器
     * @param <T>         数据类型
     *
     * @return 多条件比较器
     */
    @SafeVarargs
    public static <T> Comparator<T> ofMulti(Comparator<T>... comparators) {
        return (o1, o2) -> {
            for (Comparator<T> comparator : comparators) {
                int result = comparator.compare(o1, o2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        };
    }


    /**
     * 对象排序比较，如果对象或属性值为 null，将优先排在前面
     *
     * @param o1  待比较的第一个对象
     * @param o2  待比较的第而个对象
     * @param <T> 对象类型
     *
     * @return 比较结果
     */
    public static <T extends Comparable<T>> int comparing(T o1, T o2) {
        return comparing(false, o1, o2);
    }


    /**
     * 对象排序比较，如果对象或属性值为 null，将优先排在后面
     *
     * @param o1  待比较的第一个对象
     * @param o2  待比较的第而个对象
     * @param <T> 对象类型
     *
     * @return 比较结果
     */
    public static <T extends Comparable<T>> int comparingNullBehind(T o1, T o2) {
        return comparing(true, o1, o2);
    }

    /**
     * 对象排序比较
     *
     * @param nullBehind null 值在后面（true: null 值在后面, false: null 值在前面）
     * @param o1         待比较的第一个对象
     * @param o2         待比较的第而个对象
     * @param <T>        对象类型
     *
     * @return 比较结果
     */
    public static <T extends Comparable<T>> int comparing(boolean nullBehind, T o1, T o2) {
        if (o1 == null) { return o2 == null ? 0 : (nullBehind ? 1 : -1); }
        if (o2 == null) { return nullBehind ? -1 : 1; }
        return o1.compareTo(o2);
    }

    /**
     * 对象按属性比较排序器，如果对象或属性值为 null，将优先排在前面
     *
     * @param o1               待比较的第一个对象
     * @param o2               待比较的第而个对象
     * @param propertyGetter   属性取值器
     * @param propertiesGetter 其他属性 getter
     * @param <T>              对象类型
     *
     * @return 比较结果
     */
    @SafeVarargs
    public static <T> int comparing(
        T o1,
        T o2,
        Function<? super T, ? extends Comparable<?>> propertyGetter,
        Function<? super T, ? extends Comparable<?>>... propertiesGetter
    ) { return comparing(false, o1, o2, propertyGetter, propertiesGetter); }

    /**
     * 对象按属性比较排序器，如果对象或属性值为 null，将优先排在后面
     *
     * @param o1               待比较的第一个对象
     * @param o2               待比较的第而个对象
     * @param propertyGetter   属性取值器
     * @param propertiesGetter 其他属性 getter
     * @param <T>              对象类型
     *
     * @return 比较结果
     */
    @SafeVarargs
    public static <T> int comparingNullBehind(
        T o1,
        T o2,
        Function<? super T, ? extends Comparable<?>> propertyGetter,
        Function<? super T, ? extends Comparable<?>>... propertiesGetter
    ) { return comparing(true, o1, o2, propertyGetter, propertiesGetter); }

    /**
     * 对象按属性比较排序器
     *
     * @param nullBehind       null 值在后面（true: null 值在后面, false: null 值在前面）
     * @param o1               待比较的第一个对象
     * @param o2               待比较的第而个对象
     * @param propertyGetter   属性取值器
     * @param propertiesGetter 其他属性 getter
     * @param <T>              对象类型
     *
     * @return 比较结果
     */
    @SafeVarargs
    public static <T> int comparing(
        boolean nullBehind,
        T o1,
        T o2,
        Function<? super T, ? extends Comparable<?>> propertyGetter,
        Function<? super T, ? extends Comparable<?>>... propertiesGetter
    ) {
        if (o1 == null) { return o2 == null ? 0 : (nullBehind ? 1 : -1); }
        if (o2 == null) { return nullBehind ? -1 : 1; }
        Comparable c1 = propertyGetter.apply(o1), c2 = propertyGetter.apply(o2);
        int compared = comparing(nullBehind, c1, c2);
        if (compared == 0) {
            for (Function<? super T, ? extends Comparable<?>> fn : propertiesGetter) {
                c1 = fn.apply(o1);
                c2 = fn.apply(o2);
                if ((compared = comparing(nullBehind, c1, c2)) != 0) {
                    break;
                }
            }
        }
        return compared;
    }

    /**
     * 根据对象各个属性值进行比较，null 值靠前
     *
     * @param propertyGetter   属性获取 getter
     * @param propertiesGetter 其他属性 getter
     * @param <T>              对象类型
     *
     * @return null 值靠前属性比较器
     */
    @SafeVarargs
    public static <T> Comparator<T> comparatorOf(
        Function<? super T, ? extends Comparable<?>> propertyGetter,
        Function<? super T, ? extends Comparable<?>>... propertiesGetter
    ) { return (o1, o2) -> comparing(o1, o2, propertyGetter, propertiesGetter); }

    /**
     * 根据对象各个属性值进行比较，null 值靠后
     *
     * @param propertyGetter   属性获取 getter
     * @param propertiesGetter 其他属性 getter
     * @param <T>              对象类型
     *
     * @return null 值靠后属性比较器
     */
    @SafeVarargs
    public static <T> Comparator<T> comparatorNullBehindOf(
        Function<? super T, ? extends Comparable<?>> propertyGetter,
        Function<? super T, ? extends Comparable<?>>... propertiesGetter
    ) { return (o1, o2) -> comparing(true, o1, o2, propertyGetter, propertiesGetter); }
}
