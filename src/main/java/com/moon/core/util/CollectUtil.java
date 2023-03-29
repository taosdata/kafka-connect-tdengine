package com.moon.core.util;

import com.moon.core.enums.Collects;
import com.moon.core.enums.Lists;
import com.moon.core.enums.Sets;
import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.function.BiIntFunction;
import com.moon.core.util.function.TableIntFunction;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * 集合工具类
 *
 * @author moonsky
 */
public class CollectUtil extends BaseCollectUtil {

    protected CollectUtil() { noInstanceError(); }

    /**
     * 返回集合长度
     *
     * @param collect 集合
     *
     * @return 集合长度，集合为 null 时返回 0
     */
    public final static int size(Collection collect) {
        return collect == null ? 0 : collect.size();
    }

    /**
     * 返回集合长度，当有时候集合可能使用{@link Object}表示的，这个方法可避免手动类型强转
     *
     * @param collect 集合
     *
     * @return 集合内元素总数，集合为 null 时返回 0
     */
    public final static int sizeByObject(Object collect) { return collect == null ? 0 : ((Collection) collect).size(); }

    /**
     * 返回所有集合的总长度
     *
     * @param cs 所有集合列表
     *
     * @return 所有集合的总长度，为 null 的集合长度为 0
     */
    public final static int sizeOfAll(Collection... cs) {
        if (cs == null) {
            return 0;
        }
        int size = 0, i = 0;
        for (; i < cs.length; size += size(cs[i++])) {
        }
        return size;
    }

    /**
     * 将集合转换成数组
     *
     * @param collect    集合
     * @param arrCreator 数组构造器，接收参数为集合元素总数
     * @param <T>        集合中元素数据类型
     *
     * @return 集合中元素组成的数组
     */
    public final static <T> T[] toArray(Collection<? extends T> collect, IntFunction<T[]> arrCreator) {
        return collect == null ? arrCreator.apply(0) : collect.toArray(arrCreator.apply(size(collect)));
    }

    /**
     * 将集合转换成数组，若集合为 null 返回默认值
     *
     * @param collect    集合
     * @param arrCreator 数组构造器
     * @param defaultArr 默认值数组
     * @param <T>        集合元素数据类型
     *
     * @return 数组
     */
    public final static <T> T[] toArrayOrDefault(
        Collection<? extends T> collect, IntFunction<? extends T[]> arrCreator, T[] defaultArr
    ) { return collect == null ? defaultArr : collect.toArray(arrCreator.apply(size(collect))); }

    /**
     * 将集合转换成数组
     *
     * @param collect       集合
     * @param componentType 数组元素数据类型
     * @param <E>           集合元素数据类型
     * @param <T>           返回的数组元素数据类型
     *
     * @return 数组
     */
    public final static <T, E extends T> T[] toArray(Collection<? extends E> collect, Class<? super T> componentType) {
        Object array = Array.newInstance(componentType, size(collect));
        if (collect != null) {
            collect.toArray((E[]) array);
        }
        return (T[]) array;
    }

    /**
     * 集合是否为空
     *
     * @param collect 集合
     *
     * @return 当集合为 null 或集合中不包含任何元素时返回 true，否则返回 false
     */
    public final static boolean isEmpty(Collection collect) {
        return collect == null || collect.isEmpty();
    }

    /**
     * 集合是否不为空
     *
     * @param collect 集合
     *
     * @return 当集合不等于 null 并且集合中至少包含一个元素时返回 true，否则返回 false
     */
    public final static boolean isNotEmpty(Collection collect) { return !isEmpty(collect); }

    /*
     * ---------------------------------------------------------------------------------
     * adders
     * ---------------------------------------------------------------------------------
     */

    public final static <E, C extends Collection<? super E>> C add(C collect, E element) {
        if (collect != null) {
            collect.add(element);
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C add(C collect, E element1, E element2) {
        if (collect != null) {
            collect.add(element1);
            collect.add(element2);
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, E... elements) {
        if (collect != null && elements != null) {
            for (E element : elements) {
                collect.add(element);
            }
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, Collection<? extends E> collection) {
        if (collect != null && collection != null) {
            collect.addAll(collection);
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, Iterable<? extends E> iterable) {
        if (collect != null && iterable != null) {
            if (iterable instanceof Collection) {
                collect.addAll((Collection) iterable);
            } else {
                iterable.forEach(collect::add);
            }
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, Iterator<? extends E> iterator) {
        if (collect != null && iterator != null) {
            iterator.forEachRemaining(collect::add);
        }
        return collect;
    }

    /*
     * ---------------------------------------------------------------------------------
     * converter
     * ---------------------------------------------------------------------------------
     */

    public final static <T, O, C1 extends Collection<T>> Collection<O> map(C1 src, Function<? super T, O> function) {
        return IteratorUtil.map(src, function);
    }

    /**
     * 转换集合中的每一项，并放进另一个集合中
     *
     * @param src       源集合
     * @param function  转换器
     * @param container 返回集合容器
     * @param <T>       源项数据类型
     * @param <O>       转换后的项数据类型
     * @param <C1>      源集合类型
     * @param <CR>      返回集合类型
     *
     * @return container
     *
     * @see Collects 一些常见的集合构造器，都是 JDK 中提供的
     * @see Lists 一些常见的 List 构造器，都是 JDK 中提供的
     * @see Sets 一些常见的 Set 构造器，都是 JDK 中提供的
     */
    public final static <T, O, C1 extends Collection<T>, CR extends Collection<O>> CR map(
        C1 src, Function<? super T, O> function, IntFunction<CR> container
    ) { return IteratorUtil.mapTo(src, function, container); }

    /**
     * 将多个集合合并成一个一个集合返回，返回新创建的集合
     *
     * @param collect     第一个集合，返回的集合类型尽可能与第一个集合兼容
     * @param collections 其他集合
     * @param <T>         集合中元素数据类型
     *
     * @return 合并后的集合
     */
    public final static <T> Collection<T> concat(Collection<T> collect, Collection<T>... collections) {
        return concat0(collect, collections);
    }

    public final static <T> Set<T> toSet(T... items) { return SetUtil.newSet(items); }

    public final static <T> List<T> toList(T... items) { return ListUtil.newList(items); }

    /*
     * ---------------------------------------------------------------------------------
     * contains
     * ---------------------------------------------------------------------------------
     */

    /**
     * 集合是否包含指定元素
     *
     * @param collect 集合
     * @param item    待测元素
     *
     * @return 当集合包含指定元素时，返回 true，否则返回 false
     */
    public final static boolean contains(Collection collect, Object item) {
        return collect != null && collect.contains(item);
    }

    /**
     * 集合是否包含 item1 和 item2 中至少一个
     *
     * @param collect 集合
     * @param item1   元素 1
     * @param item2   元素 2
     *
     * @return 当集合至少包含元素 1 和元素 2 中至少一个时返回 true，否则返回 false
     */
    public final static boolean containsAny(Collection collect, Object item1, Object item2) {
        return collect != null && (collect.contains(item1) || collect.contains(item2));
    }

    /**
     * 集合是否同时包含所有待测元素
     *
     * @param collect 集合
     * @param item1   元素 1
     * @param item2   元素 2
     *
     * @return 当集合同时包含所有待测元素是返回 true，否则返回 false
     */
    public final static boolean containsAll(Collection collect, Object item1, Object item2) {
        return collect != null && (collect.contains(item1) && collect.contains(item2));
    }

    /**
     * 集合是否至少包含待测元素中其中一个
     *
     * @param collect 集合
     * @param items   待测元素
     *
     * @return 当集合至少包含所有待测元素中至少一个时，返回 true，否则返回 false
     */
    public final static boolean containsAny(Collection collect, Object... items) {
        if (collect == null) {
            return false;
        }
        for (int i = 0, l = items.length; i < l; i++) {
            if (collect.contains(items[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 集合是否同时包含所有待测元素
     *
     * @param collect 集合
     * @param items   待测元素列表
     *
     * @return 当集合同时包含所有待测元素是返回 true，否则返回 false
     */
    public final static boolean containsAll(Collection collect, Object... items) {
        if (collect == null) {
            return false;
        }
        for (int i = 0, l = items.length; i < l; i++) {
            if (!collect.contains(items[i])) {
                return false;
            }
        }
        return true;
    }

    public final static <T> boolean containsAny(Collection<T> collect1, Collection<T> collect2) {
        if (collect1 == collect2) {
            return true;
        }
        int size1 = collect1.size(), size2 = collect2.size();
        Collection<T> large = size1 > size2 ? collect1 : collect2;
        Collection<T> small = size1 > size2 ? collect2 : collect1;
        for (T item : large) {
            if (small.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断集合 1 是否包含集合 2 中所有元素
     *
     * @param collect1 集合 1
     * @param collect2 集合 2
     *
     * @return 当集合 1 包含集合 2 中所有元素时返回 true，否则返回 false
     */
    public final static boolean containsAll(Collection collect1, Collection collect2) {
        return collect1 == collect2 || (collect1 != null && collect1.containsAll(collect2));
    }

    /**
     * 聚合函数，参照 JavaScript 中 Array.reduce(..) 实现
     * <pre>
     * 1. 接受一个集合作为源数据；
     * 2. 一个处理器，处理器接收三个参数（总值, 当前项, 索引）；
     * 3. 初始值，作为第一次传入处理器的参数，也是最后返回结果
     * </pre>
     *
     * @param iterable   源数据集合
     * @param reducer    聚合函数
     * @param totalValue 返回结果
     * @param <T>        返回值类型
     * @param <E>        迭代器单项数据类型
     *
     * @return 返回最后一项处理完后的结果
     *
     * @see #reduce(Iterator, TableIntFunction, Object)
     * @see ArrayUtil#reduce(Object[], TableIntFunction, Object)
     * @see ArrayUtil#reduce(int, BiIntFunction, Object)
     */
    public final static <T, E> T reduce(
        Iterable<? extends E> iterable, TableIntFunction<? super T, ? super E, T> reducer, T totalValue
    ) {
        if (iterable != null) {
            int index = 0;
            for (E item : iterable) {
                totalValue = reducer.apply(totalValue, item, index++);
            }
        }
        return totalValue;
    }

    /**
     * 聚合函数，参照 JavaScript 中 Array.reduce(..) 实现
     * <pre>
     * 1. 接受一个集合作为源数据；
     * 2. 一个处理器，处理器接收三个参数（总值, 当前项, 索引）；
     * 3. 初始值，作为第一次传入处理器的参数，也是最后返回结果
     * </pre>
     *
     * @param iterator   源数据集合
     * @param reducer    聚合函数
     * @param totalValue 返回结果
     * @param <T>        返回值类型
     * @param <E>        迭代器单项数据类型
     *
     * @return 返回最后一项处理完后的结果
     *
     * @see #reduce(Iterable, TableIntFunction, Object)
     * @see ArrayUtil#reduce(Object[], TableIntFunction, Object)
     * @see ArrayUtil#reduce(int, BiIntFunction, Object)
     */
    public final static <T, E> T reduce(
        Iterator<? extends E> iterator, TableIntFunction<? super T, ? super E, T> reducer, T totalValue
    ) {
        if (iterator != null) {
            for (int i = 0; iterator.hasNext(); i++) {
                totalValue = reducer.apply(totalValue, iterator.next(), i);
            }
        }
        return totalValue;
    }

    /**
     * 要求空集合，即集合中至少有一项数据
     *
     * @param collect 待测集合
     * @param message 自定义消息模板
     * @param <C>     集合泛型类型
     *
     * @return 若集合中至少有一项数据时返回集合本身
     *
     * @throws IllegalArgumentException 若集合为 null 或长度为 0 时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参集合
     */
    final static <E, C extends Collection<E>> C requireNotEmpty(C collect, String message) {
        return ValidateUtil.requireNotEmpty(collect, message);
    }
}
