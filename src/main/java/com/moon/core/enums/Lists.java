package com.moon.core.enums;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.*;

import static com.moon.core.lang.ObjectUtil.defaultIfNull;
import static com.moon.core.util.FilterUtil.nullableFind;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public enum Lists implements Collects,
                             Supplier<Collection>,
                             IntFunction<Collection>,
                             Function<Collection, Collection>,
                             EnumDescriptor {
    /*
     * ----------------------------------------------------------------------------
     * List
     * ----------------------------------------------------------------------------
     */

    /**
     * ArrayList 是基于数组实现的集合 内部用数组保存所有集合项，初始容量为 10。
     * 也可自定义初始容量，最大容量为 Integer.MAX_VALUE - 8 主动自定义初始容量在一定情况下有助于提升性能
     * 每次添加（increment）、获取（getSheet）、删除（remove）、插入（insert）等操作均会检查位置或容量时候足够
     * - 不够的情况会进行扩容，每次扩容大小为上一次容量的 1.5 倍
     * - {@link java.util.ArrayList#grow(int)}扩容具体执行方法
     * - {@link ArrayList#ensureCapacity(int)} 一次性扩容至指定长度
     * - 扩容会影响性能，故推荐指定初始话大小
     * <p>
     * 迭代： ArrayList 的各种迭代方式效率区别并不大，不会出现指数级或者断崖式的差异，
     * 但是不同迭代仍然有微小差异，下面列出一个测试结果（ms, size=100）
     * ----------------------------------------------------------------------
     * | 次数(万)     | 1   | 10  | 100  | 1000  | 10000  | 100000  | 200000 |
     * | for 循环     | 1.0 | 1.0 | 3.0  | 12.0  | 136.0  | 1254.0  | 2504.0 |
     * | foreach     | 1.0 | 1.0 | 4.0  | 17.0  | 183.0  | 1773.0  | 3547.0 |
     * | iterator()  | 1.0 | 1.0 | 4.0  | 25.0  | 254.0  | 2504.0  | 5007.0 |
     * ----------------------------------------------------------------------
     * <p>
     * 支持随机访问， 添加、获取性能均高，但注意操作带来的扩容影响效率 插入或删除效率较低，因为每次插入或删除均会有数据移动操作（除非发生在尾部）
     * <p>
     * 比较： {@link #Vectors}
     * <p>
     * 继承结构：
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see List
     * @see RandomAccess
     * @see AbstractCollection
     * @see AbstractList
     */
    ArrayLists(ArrayList.class) {
        @Override
        public ArrayList get() { return new ArrayList(); }

        @Override
        public ArrayList apply(int initCapacity) { return new ArrayList(initCapacity); }

        @Override
        public ArrayList apply(Collection collection) { return new ArrayList(collection); }
    },
    /**
     * LinkedList 是基于双向链表实现的集合
     * 事实上它还可实现队列（FIFO）、栈（LIFO）、双向队列 等功能（LinkedHashMap 可实现 LRU 功能）
     * LinkedList 是一个添加、插入、删除速度均非常优秀的集合，但随机获取的性能比较差
     * 故实际上 LinkedList 的插入和删除性能均一般，因为这两个操作之前均会执行一次随机访问
     * 由于 LinkedList 是双向队列，在实现随机访问中做了一个小优化：
     * - 判断访问位置是在集合的前半部分还是后半部分{@link LinkedList#node(int)}，
     * - 然后分别前头部或尾部开始遍历
     * <p>
     * 迭代： LinkedList 的各种迭代方式效率有较为明显的差异，这主要是由于其链表结构造成的
     * 但是不同迭代仍然有微小差异，下面列出一个测试结果（ms, size=100）
     * --------------------------------------------------------------------------
     * | 次数(万)     | 1    | 10   | 100  | 1000  | 10000  | 1000000 | 2000000  |
     * | for 循环     | 10.0 | 106  | 1025 | 10227 | 102388 | 1024562 | 没跑完    |
     * | foreach     | 3.0  | 10.0 | 76.0 | 758.0 | 7643.0 | 76833.0 | 152356.0 |
     * | iterator()  | 3.0  | 9.0  | 75.0 | 756.0 | 7588.0 | 75562.0 | 152349.0 |
     * --------------------------------------------------------------------------
     * <p>
     * 继承结构：
     *
     * @see Iterable 所有集合的祖宗
     * - {@link Iterable#iterator()}
     * - {@link Iterable#forEach(Consumer)}
     * - {@link  Iterable#spliterator()}
     * @see Collection extends {@link Iterable}
     * - {@link Collection#add(Object)}
     * - {@link Collection#addAll(Collection)}
     * - {@link Collection#remove(Object)} 删除指定项
     * - {@link Collection#removeIf(Predicate)} 删除符合条件的
     * - {@link Collection#removeAll(Collection)} 删除所有
     * - {@link Collection#toArray()}
     * - {@link Collection#toArray(Object[])}转换为指定类型数组
     * - {@link Collection#contains(Object)}
     * - {@link Collection#containsAll(Collection)}
     * - {@link Collection#size()}
     * - {@link Collection#clear()}
     * - {@link Collection#isEmpty()}
     * - {@link Collection#retainAll(Collection)}
     * - {@link Collection#stream()}
     * @see List extends {@link Collection}
     * - {@link List#indexOf(Object)}
     * - {@link List#lastIndexOf(Object)}
     * - {@link List#sort(Comparator)} 排序
     * - {@link List#remove(int)} 删除指定下标元素
     * - {@link List#listIterator()} 迭代器
     * - {@link List#listIterator(int)} 从指定位置开始的迭代器
     * - {@link List#subList(int, int)}
     * - {@link List#parallelStream()}
     * @see Queue extends {@link List}
     * - {@link Queue#remove()} 删除
     * - {@link Queue#peek()}
     * - {@link Queue#poll()}
     * - {@link Queue#offer(Object)} 尾部追加 + return false
     * - {@link Queue#element()}
     * @see Deque extends {@link Queue}
     * - {@link Deque#addFirst(Object)} 头部追加 + 抛出异常
     * - {@link Deque#addLast(Object)} 尾部追加 + 抛出异常
     * - {@link Deque#offerFirst(Object)} 头部追加 + return false
     * - {@link Deque#offerLast(Object)} 尾部追加 + return false
     * - {@link Deque#peekFirst()}
     * - {@link Deque#peekLast()}
     * - {@link Deque#pollFirst()}
     * - {@link Deque#pollLast()}
     * - {@link Deque#push(Object)}
     * - {@link Deque#pop()}
     * @see AbstractList
     * @see AbstractSequentialList
     * @see java.io.Serializable
     * @see Cloneable
     * <p>
     * LinkedList 并未继承此类，但可实现此功能
     * @see Stack
     */
    LinkedLists(LinkedList.class) {
        @Override
        public LinkedList get() { return new LinkedList(); }

        @Override
        public LinkedList apply(int initCapacity) { return get(); }

        @Override
        public LinkedList apply(Collection collection) { return new LinkedList(collection); }
    },

    /**
     * Vector 与 ArrayList 有相同的继承关系，但是是一个线程安全的集合 两者的区别主要有三点： 1、Vector 所有读写方法均加了关键字 synchronized，保证了多线程下数据的安全性 2、Vector
     * 有自己独有的对外接口：element，功能与 increment、remove 等基本一致 - 这些接口同样也加了关键字 synchronized 保证数据安全 3、Vector 的扩容方式与 ArrayList 不同： -
     * ArrayList 每次扩容为员容量的 1.5 倍； - Vector 可指定每次扩容大小，或扩容为原容量的 2 倍；
     * <p>
     * 继承结构：(Vector 与 ArrayList 有相同的继承关系)
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see List
     * @see RandomAccess
     * @see AbstractCollection
     * @see AbstractList
     */
    Vectors(Vector.class) {
        @Override
        public Vector get() { return new Vector(); }

        @Override
        public Vector apply(int size) { return new Vector(size); }

        @Override
        public Vector apply(Collection collection) { return new Vector(collection); }
    },


    /**
     * Stack 直接继承自 Vector，同样是一个线程安全的集合 Stack 作为 “栈” 的数据结构，
     * 有自己的公共接口来保证 FILO 特性：
     * - {@link Stack#peek()}
     * - {@link Stack#pop()}
     * - {@link Stack#push(Object)}
     * - {@link Stack#empty()}
     * 继承结构：(Stack 直接继承自 Vector，故具有与 Vector 相同的结构)
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see List
     * @see RandomAccess
     * @see AbstractCollection
     * @see AbstractList
     * @see Vector
     */
    Stacks(Stack.class) {
        @Override
        public Stack get() { return new Stack(); }

        @Override
        public Stack apply(int value) { return get(); }

        @Override
        public Stack apply(Collection collection) {
            Stack c = get();
            c.addAll(collection);
            return c;
        }
    },
    CopyOnWriteArrayLists(CopyOnWriteArrayList.class){
        @Override
        public CopyOnWriteArrayList get() { return new CopyOnWriteArrayList(); }

        @Override
        public CopyOnWriteArrayList apply(int value) { return new CopyOnWriteArrayList(); }

        @Override
        public CopyOnWriteArrayList apply(Collection collection) {
            return new CopyOnWriteArrayList(collection);
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

        final static HashMap<Class, Lists> CACHE = new HashMap();
    }

    private final Class type;

    Lists(Class type) {
        CtorCached.CACHE.put(this.type = type, this);
        CollectsCached.put(type, this);
    }

    @Override
    public Class type() { return type; }

    /**
     * 从集合类名获取映射实例，不存在返回 null
     *
     * @param type List 集合类
     *
     * @return 查找到的对象或 null
     */
    public static Lists getOrNull(Class type) { return CtorCached.CACHE.get(type); }

    /**
     * 从对象获取映射，不存在返回 null
     *
     * @param list List 集合对象
     *
     * @return
     */
    public static Lists getOrNull(Object list) { return list == null ? null : getOrNull(list.getClass()); }

    /**
     * 从集合类名获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param type List 集合类
     *
     * @return
     */
    public static Lists getAsSuperOrNull(Class type) {
        for (Lists collect; type != null; type = type.getSuperclass()) {
            if ((collect = getOrNull(type)) != null) {
                return collect;
            }
        }
        return null;
    }

    /**
     * 从对象获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param list List 集合对象
     *
     * @return
     */
    public static Lists getAsSuperOrNull(Object list) {
        return list == null ? null : getAsSuperOrNull(list.getClass());
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType
     *
     * @param type        List 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    public static Lists getOrDefault(Class type, Lists defaultType) {
        return CtorCached.CACHE.getOrDefault(type, defaultType);
    }

    /**
     * 从对象获取映射，不存在返回 defaultType
     *
     * @param list        List 集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    public static Lists getOrDefault(Object list, Lists defaultType) {
        return list == null ? defaultType : getOrDefault(list.getClass(), defaultType);
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType 此方法会一直追溯集合类的超类，直至 Object.class 为止返回 defaultType
     *
     * @param type        List 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    public static Lists getAsSuperOrDefault(Class type, Lists defaultType) {
        for (Lists collect; type != null; type = type.getSuperclass()) {
            if ((collect = getOrNull(type)) != null) {
                return collect;
            }
        }
        return defaultType;
    }

    /**
     * 从对象获取映射，不存在返回 defaultType 此方法会一直追溯对象的超类，直至 Object.class 为止返回 defaultType
     *
     * @param list        集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    public static Lists getAsSuperOrDefault(Object list, Lists defaultType) {
        return list == null ? defaultType : getAsSuperOrDefault(list.getClass(), defaultType);
    }

    /**
     * 可以自动推断
     *
     * @param type List 集合类
     *
     * @return
     */
    public static Lists deduce(Class<? extends List> type) {
        return deduceOrDefault(type, ArrayLists);
    }

    /**
     * 可以自动推断
     *
     * @param listType List 集合类
     *
     * @return
     */
    public static Lists deduceOrDefault(Class listType, Lists type) {
        Lists collect = getAsSuperOrNull(listType);
        if (collect == null && listType != null) {
            Lists find = nullableFind(values(), item -> item.type().isAssignableFrom(listType));
            return defaultIfNull(find, type);
        }
        return collect;
    }

    /**
     * 可以自动推断
     *
     * @param list List 集合对象
     *
     * @return
     */
    public static Lists deduce(Object list) {
        return deduceOrDefault(list, ArrayLists);
    }

    /**
     * 可以自动推断
     *
     * @param list List 集合对象
     *
     * @return
     */
    public static Lists deduceOrDefault(Object list, Lists type) {
        Lists collect = getAsSuperOrNull(list);
        if (collect == null && list != null) {
            return defaultIfNull(nullableFind(values(), item -> item.type().isInstance(list)), type);
        }
        return collect;
    }
}
