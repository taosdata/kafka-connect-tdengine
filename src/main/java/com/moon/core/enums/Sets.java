package com.moon.core.enums;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static com.moon.core.lang.ObjectUtil.defaultIfNull;
import static com.moon.core.util.FilterUtil.nullableFind;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public enum Sets implements Collects,
                            Supplier<Collection>,
                            IntFunction<Collection>,
                            Function<Collection, Collection>,
                            EnumDescriptor {


    /*
     * ----------------------------------------------------------------------------
     * Set
     * ----------------------------------------------------------------------------
     */

    /**
     * TreeSet 是基于 TreeMap 实现的，详见：{@link TreeMap}、{@link Maps#TreeMap} 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see Set
     * @see SortedSet
     * @see NavigableSet
     * @see Cloneable
     * @see java.io.Serializable
     * @see AbstractCollection
     * @see AbstractSet
     */
    TreeSets(TreeSet.class) {
        @Override
        public TreeSet get() { return new TreeSet(); }

        @Override
        public TreeSet apply(int initCapacity) { return new TreeSet(); }

        @Override
        public TreeSet apply(Collection collection) { return new TreeSet(collection); }
    },
    /**
     * HashSet 基于散列表 {@link HashMap} 实现的 HashSet 是一个高效的集合，它通过 hashCode 和 equals 维护一个无序不重复的集合 关系： - HashSet 里的每一项是
     * HashMap 的键 - 所有键对应的值都指向同一个对象 {@link HashSet#PRESENT} - {@link HashMap#put(Object, Object)} - {@link
     * HashSet#add(Object)}
     * <p>
     * - 实际上 HashSet 自身也能实现一个有序的集合{@link HashSet#(int, float, boolean)} 这个构造器中的第三个参数 dummy，并没有任何实际作用， &nbsp;
     * 只是用来标记通过此构造方法得到的是一个用{@link LinkedHashMap}维护数据而不是{@link HashMap} &nbsp;  但这个构造器是用 default 修饰的，无法被外界调用， &nbsp;
     * 详见：{@link LinkedHashSet}、{@link #LinkedHashSet}、{@link Maps#LinkedHashMap}
     * <p>
     * 详解 {@link HashMap}
     * <p>
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see Set
     * @see AbstractCollection
     * @see AbstractSet
     * @see Cloneable
     * @see java.io.Serializable
     */
    HashSets(HashSet.class) {
        @Override
        public HashSet get() { return new HashSet(); }

        @Override
        public HashSet apply(int initCapacity) { return new HashSet(initCapacity); }

        @Override
        public HashSet apply(Collection collection) { return new HashSet(collection); }
    },
    /**
     * 基于 {@link LinkedHashMap} 实现 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see Set
     * @see AbstractCollection
     * @see AbstractSet
     * @see HashSet
     * @see Cloneable
     * @see java.io.Serializable
     */
    LinkedHashSets(LinkedHashSet.class) {
        @Override
        public LinkedHashSet get() { return new LinkedHashSet(); }

        @Override
        public LinkedHashSet apply(int initCapacity) { return new LinkedHashSet(initCapacity); }

        @Override
        public LinkedHashSet apply(Collection collection) { return new LinkedHashSet(collection); }
    },

    CopyOnWriteArraySets(CopyOnWriteArraySet.class) {
        @Override
        public CopyOnWriteArraySet get() { return new CopyOnWriteArraySet(); }

        @Override
        public CopyOnWriteArraySet apply(int value) { return new CopyOnWriteArraySet(); }

        @Override
        public CopyOnWriteArraySet apply(Collection collection) {
            return new CopyOnWriteArraySet(collection);
        }
    },
    ConcurrentSkipListSets(ConcurrentSkipListSet.class) {
        @Override
        public ConcurrentSkipListSet get() {
            return new ConcurrentSkipListSet();
        }

        @Override
        public ConcurrentSkipListSet apply(int value) {
            return new ConcurrentSkipListSet();
        }

        @Override
        public ConcurrentSkipListSet apply(Collection collection) {
            return new ConcurrentSkipListSet(collection);
        }
    },
    ;

    /**
     * 枚举信息
     *
     * @return
     */
    @Override
    public final String getText() { return type.getName(); }

    static final class CtorCached {

        final static HashMap<Class, Sets> CACHE = new HashMap();
    }

    private final Class type;

    Sets(Class type) {
        CtorCached.CACHE.put(this.type = type, this);
        CollectsCached.put(type, this);
    }

    @Override
    public Class type() { return type; }

    /**
     * 从集合类名获取映射实例，不存在返回 null
     *
     * @param type Set 集合类
     *
     * @return 查找到的对象或 null
     */
    public static Sets getOrNull(Class type) { return Sets.CtorCached.CACHE.get(type); }

    /**
     * 从对象获取映射，不存在返回 null
     *
     * @param set Set 集合对象
     *
     * @return
     */
    public static Sets getOrNull(Object set) { return set == null ? null : getOrNull(set.getClass()); }

    /**
     * 从集合类名获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param type Set 集合类
     *
     * @return
     */
    public static Sets getAsSuperOrNull(Class type) {
        for (Sets collect; type != null; type = type.getSuperclass()) {
            if ((collect = getOrNull(type)) != null) {
                return collect;
            }
        }
        return null;
    }

    /**
     * 从对象获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param set Set 集合对象
     *
     * @return
     */
    public static Sets getAsSuperOrNull(Object set) {
        return set == null ? null : getAsSuperOrNull(set.getClass());
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType
     *
     * @param type        Set 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    public static Sets getOrDefault(Class type, Sets defaultType) {
        return Sets.CtorCached.CACHE.getOrDefault(type, defaultType);
    }

    /**
     * 从对象获取映射，不存在返回 defaultType
     *
     * @param set         Set 集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    public static Sets getOrDefault(Object set, Sets defaultType) {
        return set == null ? defaultType : getOrDefault(set.getClass(), defaultType);
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType 此方法会一直追溯集合类的超类，直至 Object.class 为止返回 defaultType
     *
     * @param type        Set 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    public static Sets getAsSuperOrDefault(Class type, Sets defaultType) {
        for (Sets collect; type != null; type = type.getSuperclass()) {
            if ((collect = getOrNull(type)) != null) {
                return collect;
            }
        }
        return defaultType;
    }

    /**
     * 从对象获取映射，不存在返回 defaultType 此方法会一直追溯对象的超类，直至 Object.class 为止返回 defaultType
     *
     * @param set         集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    public static Sets getAsSuperOrDefault(Object set, Sets defaultType) {
        return set == null ? defaultType : getAsSuperOrDefault(set.getClass(), defaultType);
    }

    /**
     * 可以自动推断
     *
     * @param type Set 集合类
     *
     * @return
     */
    public static Sets deduce(Class<? extends Set> type) {
        return deduceOrDefault(type, HashSets);
    }

    /**
     * 可以自动推断
     *
     * @param setType Set 集合类
     *
     * @return
     */
    public static Sets deduceOrDefault(Class setType, Sets type) {
        Sets collect = getAsSuperOrNull(setType);
        if (collect == null && setType != null) {
            Sets find = nullableFind(values(), item -> item.type().isAssignableFrom(setType));
            return defaultIfNull(find, type);
        }
        return collect;
    }

    /**
     * 可以自动推断
     *
     * @param set Set 集合对象
     *
     * @return
     */
    public static Sets deduce(Object set) {
        return deduceOrDefault(set, HashSets);
    }

    /**
     * 可以自动推断
     *
     * @param set Set 集合对象
     *
     * @return
     */
    public static Sets deduceOrDefault(Object set, Sets type) {
        Sets collect = getAsSuperOrNull(set);
        if (collect == null && set != null) {
            return defaultIfNull(nullableFind(values(), item -> item.type().isInstance(set)), type);
        }
        return collect;
    }
}
