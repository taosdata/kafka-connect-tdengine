package com.moon.core.util;

import com.moon.core.enums.Collects;
import com.moon.core.enums.Testers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class FilterUtil {

    private FilterUtil() { noInstanceError(); }

    /*
     * --------------------------------------------------------------
     * find one
     * --------------------------------------------------------------
     */

    /**
     * 返回第一个非 null 元素
     *
     * @param ts  数据列表
     * @param <T> 数据类型
     *
     * @return 如果存在第一个非 null 元素，则返回该元素，否则返回 null
     */
    public static <T> T nullableFirst(T... ts) {
        return nullableFind(ts, (Predicate<? super T>) Testers.isNotNull);
    }

    /**
     * 合并多个“且”检查条件
     *
     * @param testers 检查列表
     * @param <T>     数据类型
     *
     * @return 合并后的检查器
     */
    public static <T> Predicate<T> ofAll(Predicate<T>... testers) {
        return value -> {
            for (Predicate<T> tester : testers) {
                if (!tester.test(value)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * 合并多个“或”检查条件
     *
     * @param testers 检查列表
     * @param <T>     数据类型
     *
     * @return 合并后的检查器
     */
    public static <T> Predicate<T> ofAny(Predicate<T>... testers) {
        return value -> {
            for (Predicate<T> tester : testers) {
                if (tester.test(value)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * 在数组中查找符合条件的第一项
     *
     * @param arr    查找范围数组
     * @param tester 匹配条件
     * @param <T>    数组单项数据类型
     *
     * @return 返回 optional
     */
    public static <T> java.util.Optional<T> findAsUtilOptional(T[] arr, Predicate<? super T> tester) {
        return java.util.Optional.ofNullable(nullableFind(arr, tester));
    }

    /**
     * 在集合中查找符合条件的第一项
     *
     * @param iterable 查找范围集合
     * @param tester   匹配条件
     * @param <T>      集合单项数据类型
     *
     * @return 返回 optional
     */
    public static <T> java.util.Optional<T> findAsUtilOptional(Iterable<T> iterable, Predicate<? super T> tester) {
        return java.util.Optional.ofNullable(nullableFind(iterable, tester));
    }

    /**
     * 在数组中查找符合条件的第一项
     *
     * @param arr    查找范围数组
     * @param tester 匹配条件
     * @param <T>    数组单项数据类型
     *
     * @return 返回 optional
     */
    public static <T> Optional<T> find(T[] arr, Predicate<? super T> tester) {
        return Optional.ofNullable(nullableFind(arr, tester));
    }

    /**
     * 在集合中查找符合条件的第一项
     *
     * @param iterable 查找范围集合
     * @param tester   匹配条件
     * @param <T>      集合单项数据类型
     *
     * @return 返回 optional
     */
    public static <T> Optional<T> find(Iterable<T> iterable, Predicate<? super T> tester) {
        return Optional.ofNullable(nullableFind(iterable, tester));
    }

    /**
     * 在集合中查找符合条件的第一项
     *
     * @param iterable             查找范围集合
     * @param tester               匹配条件
     * @param defaultValueSupplier 默认值
     * @param <T>                  集合单项数据类型
     *
     * @return 如果集合中存在符合条件的数据，则返回对应数据；否则返回默认值 defaultValue
     */
    public static <T> T findOrPull(
        Iterable<T> iterable, Predicate<? super T> tester, Supplier<T> defaultValueSupplier
    ) {
        if (iterable == null) {
            return defaultValueSupplier.get();
        }
        for (T item : iterable) {
            if (tester.test(item)) {
                return item;
            }
        }
        return defaultValueSupplier.get();
    }

    /**
     * 在数组中查找符合条件的第一项
     *
     * @param arr          查找范围数组
     * @param tester       匹配条件
     * @param defaultValue 默认值
     * @param <T>          数组单项数据类型
     *
     * @return 如果数据中存在符合条件的数据，则返回对应数据；否则返回默认值 defaultValue
     */
    public static <T> T findOrDefault(T[] arr, Predicate<? super T> tester, T defaultValue) {
        if (arr == null) {
            return defaultValue;
        }
        for (T item : arr) {
            if (tester.test(item)) {
                return item;
            }
        }
        return defaultValue;
    }


    /**
     * 在集合中查找符合条件的第一项
     *
     * @param iterable     查找范围集合
     * @param tester       匹配条件
     * @param defaultValue 默认值
     * @param <T>          集合单项数据类型
     *
     * @return 如果集合中存在符合条件的数据，则返回对应数据；否则返回默认值 defaultValue
     */
    public static <T> T findOrDefault(Iterable<T> iterable, Predicate<? super T> tester, T defaultValue) {
        if (iterable == null) {
            return defaultValue;
        }
        for (T item : iterable) {
            if (tester.test(item)) {
                return item;
            }
        }
        return defaultValue;
    }

    /**
     * 在数组中查找符合条件的第一项
     *
     * @param arr    查找范围数组
     * @param tester 匹配条件
     * @param <T>    数组单项数据类型
     *
     * @return 如果数据中存在符合条件的数据，则返回对应数据；否则返回 null
     */
    public static <T> T nullableFind(T[] arr, Predicate<? super T> tester) {
        return findOrDefault(arr, tester, null);
    }

    /**
     * 在集合中查找符合条件的第一项
     *
     * @param iterable 查找范围集合
     * @param tester   匹配条件
     * @param <T>      集合单项数据类型
     *
     * @return 如果集合中存在符合条件的数据，则返回对应数据；否则返回 null
     */
    public static <T> T nullableFind(Iterable<T> iterable, Predicate<? super T> tester) {
        return findOrDefault(iterable, tester, null);
    }

    /**
     * 在数组中查找符合条件的数据，要求必须存在，否则抛出异常
     *
     * @param arr    查找范围数组
     * @param tester 匹配条件
     * @param <T>    集合单项数据类型
     *
     * @return 如果数组中存在符合条件的数据，则返回第一个匹配数据；否则抛出异常
     *
     * @throws IllegalArgumentException 当数组中不存在符合条件的数据时抛出异常
     */
    public static <T> T requireFind(T[] arr, Predicate<? super T> tester) {
        return requireFind(arr, tester, "Can not find matches item");
    }

    /**
     * 在数组中查找符合条件的数据，要求必须存在，否则抛出异常
     *
     * @param arr          查找范围数组
     * @param tester       匹配条件
     * @param errorMessage 错误消息
     * @param <T>          集合单项数据类型
     *
     * @return 如果数组中存在符合条件的数据，则返回第一个匹配数据；否则抛出异常
     *
     * @throws IllegalArgumentException 当数组中不存在符合条件的数据时抛出异常
     */
    public static <T> T requireFind(T[] arr, Predicate<? super T> tester, String errorMessage) {
        if (arr == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        for (T item : arr) {
            if (tester.test(item)) {
                return item;
            }
        }
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * 在集合中查找符合条件的数据，要求必须存在，否则抛出异常
     *
     * @param iterable 查找范围集合
     * @param tester   匹配条件
     * @param <T>      集合单项数据类型
     *
     * @return 如果集合中存在符合条件的数据，则返回第一个匹配数据；否则抛出异常
     *
     * @throws IllegalArgumentException 当集合中不存在符合条件的数据时抛出异常
     */
    public static <T> T requireFind(Iterable<T> iterable, Predicate<? super T> tester) {
        return requireFind(iterable, tester, "Can not find matches item");
    }

    /**
     * 在集合中查找符合条件的数据，要求必须存在，否则抛出异常
     *
     * @param iterable     查找范围集合
     * @param tester       匹配条件
     * @param errorMessage 错误消息
     * @param <T>          集合单项数据类型
     *
     * @return 如果集合中存在符合条件的数据，则返回第一个匹配数据；否则抛出异常
     *
     * @throws IllegalArgumentException 当集合中不存在符合条件的数据时抛出异常
     */
    public static <T> T requireFind(Iterable<T> iterable, Predicate<? super T> tester, String errorMessage) {
        if (iterable == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        for (T item : iterable) {
            if (tester.test(item)) {
                return item;
            }
        }
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * 在集合中查找符合条件的数据，要求必须存在，否则抛出异常
     *
     * @param iterable          查找范围集合
     * @param tester            匹配条件
     * @param throwableSupplier 错误消息
     * @param <T>               集合单项数据类型
     *
     * @return 如果集合中存在符合条件的数据，则返回第一个匹配数据；否则抛出异常
     *
     * @throws IllegalArgumentException 当集合中不存在符合条件的数据时抛出异常
     */
    public static <T, EX extends Throwable> T requireFind(
        Iterable<T> iterable, Predicate<? super T> tester, Supplier<EX> throwableSupplier
    ) throws EX {
        if (iterable == null) {
            throw throwableSupplier.get();
        }
        for (T item : iterable) {
            if (tester.test(item)) {
                return item;
            }
        }
        throw throwableSupplier.get();
    }

    /*
     * --------------------------------------------------------------
     * filter
     * --------------------------------------------------------------
     */

    /**
     * 从数组中过滤出符合条件的数据项，并以{@link ArrayList}形式返回
     *
     * @param es     数组
     * @param tester 匹配条件
     * @param <E>    数组单项数据类型
     *
     * @return 返回数组中符合条件的所有项集合，确保返回结果不为 null（至少是个空集合）
     */
    public static <E> ArrayList<E> filter(E[] es, Predicate<? super E> tester) {
        return filter(es, tester, new ArrayList<>());
    }


    /**
     * 从数组中过滤出符合条件的数据项，并以{@link ArrayList}形式返回
     *
     * @param es     数组
     * @param tester 匹配条件
     * @param <E>    数组单项数据类型
     *
     * @return 返回数组中符合条件的所有项集合
     *
     * @throws NullPointerException tester 或 container 为 null 的话，可能抛出 NPE
     */
    public static <E, C extends Collection<? super E>> C filter(
        E[] es, Predicate<? super E> tester, C container
    ) {
        if (es != null) {
            for (int i = 0, len = es.length; i < len; i++) {
                if (tester.test(es[i])) {
                    container.add(es[i]);
                }
            }
        }
        return container;
    }

    /**
     * 按条件 tester 过滤出集合 list 中的所有匹配项，以 List 形式返回
     *
     * @param list   过滤范围集合
     * @param tester 过滤规则
     * @param <E>    集合项数据类型
     *
     * @return 符合过滤规则的数据集合
     */
    public static <E> List<E> filter(List<E> list, Predicate<? super E> tester) {
        final Supplier supplier = Collects.getOrDefault(list, Collects.ArrayLists);
        return (List) filter(list, tester, supplier);
    }

    /**
     * 按条件 tester 过滤出集合 list 中的所有匹配项，以 List 形式返回
     *
     * @param list    过滤范围集合
     * @param testers 过滤规则，多条件是“and”的关系
     * @param <E>     集合项数据类型
     *
     * @return 符合过滤规则的数据集合
     */
    public static <E> List<E> filterOfAll(List<E> list, Predicate<? super E>... testers) {
        final Supplier supplier = Collects.getOrDefault(list, Collects.HashSets);
        return (List) filterOfAll(list, supplier, testers);
    }

    /**
     * 按条件 tester 过滤出集合 list 中的所有匹配项，以 List 形式返回
     *
     * @param list    过滤范围集合
     * @param testers 过滤规则，多条件是“and”的关系
     * @param <E>     集合项数据类型
     *
     * @return 符合过滤规则的数据集合
     */
    public static <E> List<E> filterOfAny(List<E> list, Predicate<? super E>... testers) {
        final Supplier supplier = Collects.getOrDefault(list, Collects.HashSets);
        return (List) filterOfAny(list, supplier, testers);
    }

    /**
     * 按条件 tester 过滤出集合 set 中的所有匹配项，以 Set 形式返回
     *
     * @param set    过滤范围集合
     * @param tester 过滤规则
     * @param <E>    集合项数据类型
     *
     * @return 符合过滤规则的数据集合
     */
    public static <E> Set<E> filter(Set<E> set, Predicate<? super E> tester) {
        final Supplier supplier = Collects.getOrDefault(set, Collects.HashSets);
        return (Set) filter(set, tester, supplier);
    }

    /**
     * 按条件 tester 过滤出集合 set 中的所有匹配项，以 Set 形式返回
     *
     * @param set     过滤范围集合
     * @param testers 过滤规则，多条件是“and”的关系
     * @param <E>     集合项数据类型
     *
     * @return 符合过滤规则的数据集合
     */
    public static <E> Set<E> filterOfAll(Set<E> set, Predicate<? super E>... testers) {
        final Supplier supplier = Collects.getOrDefault(set, Collects.HashSets);
        return (Set) filterOfAll(set, supplier, testers);
    }

    /**
     * 按条件 tester 过滤出集合 set 中的所有匹配项，以 Set 形式返回
     *
     * @param set     过滤范围集合
     * @param testers 过滤规则，多条件是“and”的关系
     * @param <E>     集合项数据类型
     *
     * @return 符合过滤规则的数据集合
     */
    public static <E> Set<E> filterOfAny(Set<E> set, Predicate<? super E>... testers) {
        final Supplier supplier = Collects.getOrDefault(set, Collects.HashSets);
        return (Set) filterOfAny(set, supplier, testers);
    }

    /**
     * 按条件 tester 过滤出集合 collect 中的所有匹配项，自定义返回容器
     *
     * @param collect   过滤范围集合
     * @param tester    过滤规则
     * @param container 自定义集合容器
     * @param <E>       集合项数据类型
     * @param <CR>      自定义集合容器类型
     *
     * @return 符合过滤条件的数据集合
     */
    public static <E, CR extends Collection<? super E>> CR filter(
        Collection<E> collect, Predicate<? super E> tester, Supplier<CR> container
    ) { return filter(collect, tester, container.get()); }

    /**
     * 按条件 tester 过滤出集合 collect 中的所有匹配项，自定义返回容器
     *
     * @param collect   过滤范围集合
     * @param container 自定义集合容器
     * @param testers   过滤规则，多条件是“and”的关系
     * @param <E>       集合项数据类型
     * @param <CR>      自定义集合容器类型
     *
     * @return 符合过滤条件的数据集合
     */
    public static <E, CR extends Collection<? super E>> CR filterOfAll(
        Collection<E> collect, Supplier<CR> container, Predicate<? super E>... testers
    ) { return filterOfAll(collect, container.get(), testers); }

    /**
     * 按条件 tester 过滤出集合 collect 中的所有匹配项，自定义返回容器
     *
     * @param collect   过滤范围集合
     * @param container 自定义集合容器
     * @param testers   过滤规则，多条件是“or”的关系
     * @param <E>       集合项数据类型
     * @param <CR>      自定义集合容器类型
     *
     * @return 符合过滤条件的数据集合
     */
    public static <E, CR extends Collection<? super E>> CR filterOfAny(
        Collection<E> collect, Supplier<CR> container, Predicate<? super E>... testers
    ) { return filterOfAny(collect, container.get(), testers); }

    /**
     * 按条件 tester 过滤出集合 collect 中的匹配项，添加到集合 container 返回
     *
     * @param collect   过滤范围集合
     * @param tester    自定义集合容器
     * @param container 符合过滤条件的容器
     * @param <E>       集合项数据类型
     * @param <CR>      自定义集合容器类型
     *
     * @return 符合过滤条件的数据集合
     */
    public static <E, CR extends Collection<? super E>> CR filter(
        Iterable<E> collect, Predicate<? super E> tester, CR container
    ) {
        if (collect != null) {
            for (E item : collect) {
                if (tester.test(item)) {
                    container.add(item);
                }
            }
        }
        return container;
    }

    /**
     * 按条件 tester 过滤出集合 collect 中的所有匹配项，自定义返回容器
     *
     * @param collect   过滤范围集合
     * @param container 自定义集合容器
     * @param testers   过滤规则，多条件是“and”的关系
     * @param <E>       集合项数据类型
     * @param <CR>      自定义集合容器类型
     *
     * @return 符合过滤条件的数据集合
     */
    public static <E, CR extends Collection<? super E>> CR filterOfAll(
        Iterable<E> collect, CR container, Predicate<? super E>... testers
    ) {
        if (collect != null) {
            int i;
            final int len = testers == null ? 0 : testers.length;
            switch (len) {
                case 0:
                    return CollectUtil.addAll(container, collect);
                case 1:
                    return filter(collect, testers[0], container);
                default:
                    outer:
                    for (E item : collect) {
                        for (i = 0; i < len; i++) {
                            if (!testers[i].test(item)) {
                                continue outer;
                            }
                        }
                        container.add(item);
                    }
            }
        }
        return container;
    }

    /**
     * 按条件 tester 过滤出集合 collect 中的所有匹配项，自定义返回容器
     *
     * @param collect   过滤范围集合
     * @param container 自定义集合容器
     * @param testers   过滤规则，多条件是“or”的关系
     * @param <E>       集合项数据类型
     * @param <CR>      自定义集合容器类型
     *
     * @return 符合过滤条件的数据集合
     */
    public static <E, CR extends Collection<? super E>> CR filterOfAny(
        Iterable<E> collect, CR container, Predicate<? super E>... testers
    ) {
        if (collect != null) {
            int i;
            final int len = testers == null ? 0 : testers.length;
            switch (len) {
                case 0:
                    return CollectUtil.addAll(container, collect);
                case 1:
                    return filter(collect, testers[0], container);
                default:
                    for (E item : collect) {
                        for (i = 0; i < len; i++) {
                            if (testers[i].test(item)) {
                                container.add(item);
                                break;
                            }
                        }
                    }
            }
        }
        return container;
    }

    /*
     * --------------------------------------------------------------
     * consumer present
     * --------------------------------------------------------------
     */

    /**
     * 遍历每一个符合条件的项
     *
     * @param collect  遍历范围（集合）
     * @param tester   匹配条件
     * @param consumer 处理器
     * @param <T>      集合单项数据类型
     */
    public static <T> void forEachMatched(
        Collection<T> collect, Predicate<? super T> tester, Consumer<T> consumer
    ) {
        if (collect != null) {
            for (T item : collect) {
                if (tester.test(item)) {
                    consumer.accept(item);
                }
            }
        }
    }

    /**
     * 遍历每一个符合条件的项
     *
     * @param ts       遍历范围（数组）
     * @param tester   匹配条件
     * @param consumer 处理器
     * @param <T>      集合单项数据类型
     */
    public static <T> void forEachMatched(
        T[] ts, Predicate<? super T> tester, Consumer<T> consumer
    ) {
        final int length = ts == null ? 0 : ts.length;
        for (int i = 0; i < length; i++) {
            T item = ts[i];
            if (tester.test(item)) {
                consumer.accept(item);
            }
        }
    }
}
