package com.moon.core.enums;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static com.moon.core.lang.ObjectUtil.defaultIfNull;
import static com.moon.core.util.FilterUtil.nullableFind;

/**
 * @author moonsky
 */
public interface Collects
    extends Supplier<Collection>, IntFunction<Collection>, Function<Collection, Collection>, EnumDescriptor {

    Collects ArrayLists = Lists.ArrayLists;

    Collects LinkedLists = Lists.LinkedLists;

    Collects Vectors = Lists.Vectors;

    Collects Stacks = Lists.Stacks;

    Collects CopyOnWriteArrayLists = Lists.CopyOnWriteArrayLists;

    Collects HashSets = Sets.HashSets;

    Collects TreeSets = Sets.TreeSets;

    Collects LinkedHashSets = Sets.LinkedHashSets;

    Collects CopyOnWriteArraySets = Sets.CopyOnWriteArraySets;

    Collects ConcurrentSkipListSets = Sets.ConcurrentSkipListSets;

    Collects PriorityQueues = Queues.PriorityQueues;

    Collects LinkedBlockingQueues = Queues.LinkedBlockingQueues;

    Collects ArrayBlockingQueues = Queues.ArrayBlockingQueues;

    Collects PriorityBlockingQueues = Queues.PriorityBlockingQueues;

    Collects SynchronousQueues = Queues.SynchronousQueues;

    Collects LinkedTransferQueues = Queues.LinkedTransferQueues;

    Collects ConcurrentLinkedQueues = Queues.ConcurrentLinkedQueues;

    Collects DelayQueues = Queues.DelayQueues;

    Collects ArrayDeques = Queues.ArrayDeques;

    Collects LinkedBlockingDeques = Queues.LinkedBlockingDeques;

    Collects ConcurrentLinkedDeques = Queues.ConcurrentLinkedDeques;

    /**
     * 当前对象类型
     *
     * @return class
     */
    Class type();

    /**
     * 所有项的数组
     *
     * @return
     */
    static Collects[] values() { return CollectsCached.toValuesArr(); }

    /**
     * 从集合类名获取映射实例，不存在返回 null
     *
     * @param type Collection 集合类
     *
     * @return 查找到的对象或 null
     */
    static Collects getOrNull(Class type) { return CollectsCached.get(type); }

    /**
     * 从对象获取映射，不存在返回 null
     *
     * @param collect Collection 集合对象
     *
     * @return
     */
    static Collects getOrNull(Object collect) { return collect == null ? null : getOrNull(collect.getClass()); }

    /**
     * 从集合类名获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param type Collection 集合类
     *
     * @return
     */
    static Collects getAsSuperOrNull(Class type) {
        for (Collects collect; type != null; type = type.getSuperclass()) {
            if ((collect = getOrNull(type)) != null) {
                return collect;
            }
        }
        return null;
    }

    /**
     * 从对象获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param collect Collection 集合对象
     *
     * @return
     */
    static Collects getAsSuperOrNull(Object collect) {
        return collect == null ? null : getAsSuperOrNull(collect.getClass());
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType
     *
     * @param type        Collection 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    static Collects getOrDefault(Class type, Collects defaultType) {
        return CollectsCached.getOrDefault(type, defaultType);
    }

    /**
     * 从对象获取映射，不存在返回 defaultType
     *
     * @param collect     Collection 集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    static Collects getOrDefault(Object collect, Collects defaultType) {
        return collect == null ? defaultType : getOrDefault(collect.getClass(), defaultType);
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType 此方法会一直追溯集合类的超类，直至 Object.class 为止返回 defaultType
     *
     * @param collectType Collection 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    static Collects getAsSuperOrDefault(Class collectType, Collects defaultType) {
        for (Collects collect; collectType != null; collectType = collectType.getSuperclass()) {
            if ((collect = getOrNull(collectType)) != null) {
                return collect;
            }
        }
        return defaultType;
    }

    /**
     * 从对象获取映射，不存在返回 defaultType 此方法会一直追溯对象的超类，直至 Object.class 为止返回 defaultType
     *
     * @param collect     集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    static Collects getAsSuperOrDefault(Object collect, Collects defaultType) {
        return collect == null ? defaultType : getAsSuperOrDefault(collect.getClass(), defaultType);
    }

    /**
     * 可以自动推断
     *
     * @param collectType Collection 集合类
     *
     * @return
     */
    static Collects deduce(Class<? extends Set> collectType) {
        return deduceOrDefault(collectType, HashSets);
    }

    /**
     * 可以自动推断
     *
     * @param collectType Collection 集合类
     * @param type        默认值
     *
     * @return
     */
    static Collects deduceOrDefault(Class collectType, Collects type) {
        Collects collect = getAsSuperOrNull(collectType);
        if (collect == null && collectType != null) {
            Collects find = nullableFind(values(), item -> item.type().isAssignableFrom(collectType));
            return defaultIfNull(find, type);
        }
        return collect;
    }

    /**
     * 可以自动推断
     *
     * @param collect Collection 集合对象
     *
     * @return
     */
    static Collects deduce(Object collect) {
        return deduceOrDefault(collect, HashSets);
    }

    /**
     * 可以自动推断
     *
     * @param collect Collection 集合对象
     * @param type    默认值
     *
     * @return
     */
    static Collects deduceOrDefault(Object collect, Collects type) {
        Collects find = getAsSuperOrNull(collect);
        if (find == null && collect != null) {
            return defaultIfNull(nullableFind(values(), item -> item.type().isInstance(collect)), type);
        }
        return find;
    }
}
