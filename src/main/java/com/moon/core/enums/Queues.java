package com.moon.core.enums;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.moon.core.lang.ObjectUtil.defaultIfNull;
import static com.moon.core.util.FilterUtil.nullableFind;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public enum Queues implements Collects,
                              Supplier<Collection>,
                              IntFunction<Collection>,
                              Function<Collection, Collection>,
                              EnumDescriptor {


    /*
     * ----------------------------------------------------------------------------
     * Queue
     * ----------------------------------------------------------------------------
     */

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
     * 继承结构
     *
     * @see Iterable
     * @see Collection
     * @see Queue
     * @see AbstractCollection
     * @see AbstractQueue
     * @see java.io.Serializable
     */
    PriorityQueues(java.util.PriorityQueue.class) {
        @Override
        public PriorityQueue get() { return new PriorityQueue(); }

        @Override
        public PriorityQueue apply(int value) { return new PriorityQueue(value); }

        @Override
        public PriorityQueue apply(Collection collection) { return new PriorityQueue(collection); }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    LinkedBlockingQueues(java.util.concurrent.LinkedBlockingQueue.class) {
        @Override
        public LinkedBlockingQueue get() { return new LinkedBlockingQueue(); }

        @Override
        public LinkedBlockingQueue apply(int value) { return new LinkedBlockingQueue(value); }

        @Override
        public LinkedBlockingQueue apply(Collection collection) { return new LinkedBlockingQueue(collection); }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    ArrayBlockingQueues(java.util.concurrent.ArrayBlockingQueue.class) {
        @Override
        public ArrayBlockingQueue get() { throw new UnsupportedOperationException(); }

        @Override
        public ArrayBlockingQueue apply(int value) { return new ArrayBlockingQueue(value); }

        @Override
        public ArrayBlockingQueue apply(Collection collection) {
            if (collection == null) {
                return new ArrayBlockingQueue(16);
            } else {
                ArrayBlockingQueue collect = new ArrayBlockingQueue<>(collection.size());
                collect.addAll(collection);
                return collect;
            }
        }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    PriorityBlockingQueues(java.util.concurrent.PriorityBlockingQueue.class) {
        @Override
        public PriorityBlockingQueue get() { return new PriorityBlockingQueue(); }

        @Override
        public PriorityBlockingQueue apply(int value) { return new PriorityBlockingQueue(value); }

        @Override
        public PriorityBlockingQueue apply(Collection collection) { return new PriorityBlockingQueue(collection); }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    SynchronousQueues(java.util.concurrent.SynchronousQueue.class) {
        @Override
        public SynchronousQueue get() { return new SynchronousQueue(); }

        @Override
        public SynchronousQueue apply(int value) { return get(); }

        @Override
        public SynchronousQueue apply(Collection collection) { return get(); }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see Queue
     * @see AbstractQueue
     * @see BlockingQueue
     * @see TransferQueue
     * @see java.io.Serializable
     */
    LinkedTransferQueues(LinkedTransferQueue.class) {
        @Override
        public LinkedTransferQueue get() { return new LinkedTransferQueue(); }

        @Override
        public LinkedTransferQueue apply(int value) { return get(); }

        @Override
        public LinkedTransferQueue apply(Collection collection) { return new LinkedTransferQueue(collection); }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see Queue
     * @see AbstractQueue
     * @see java.io.Serializable
     */
    ConcurrentLinkedQueues(ConcurrentLinkedQueue.class) {
        @Override
        public ConcurrentLinkedQueue get() { return new ConcurrentLinkedQueue(); }

        @Override
        public ConcurrentLinkedQueue apply(int value) { return new ConcurrentLinkedQueue(); }

        @Override
        public ConcurrentLinkedQueue apply(Collection collection) { return new ConcurrentLinkedQueue(collection); }
    },
    DelayQueues(DelayQueue.class){
        @Override
        public DelayQueue get() {
            return new DelayQueue();
        }

        @Override
        public DelayQueue apply(int value) {
            return new DelayQueue();
        }

        @Override
        public DelayQueue apply(Collection collection) {
            return new DelayQueue(collection);
        }
    },

    /*
     * ----------------------------------------------------------------------------
     * Deque
     * ----------------------------------------------------------------------------
     */

    /**
     * 继承结构
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see Queue
     * @see Deque
     * @see AbstractCollection
     */
    ArrayDeques(ArrayDeque.class) {
        @Override
        public ArrayDeque get() { return new ArrayDeque(); }

        @Override
        public ArrayDeque apply(int value) { return new ArrayDeque(value); }

        @Override
        public ArrayDeque apply(Collection collection) { return new ArrayDeque(collection); }
    },

    /**
     * 继承结构
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see Queue
     * @see Deque
     * @see BlockingQueue
     * @see java.util.concurrent.BlockingDeque
     * @see AbstractQueue
     */
    LinkedBlockingDeques(LinkedBlockingDeque.class) {
        @Override
        public LinkedBlockingDeque get() { return new LinkedBlockingDeque(); }

        @Override
        public LinkedBlockingDeque apply(int value) { return new LinkedBlockingDeque(value); }

        @Override
        public LinkedBlockingDeque apply(Collection collection) { return new LinkedBlockingDeque(collection); }
    },

    /**
     * 继承结构
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see Queue
     * @see Deque
     * @see BlockingQueue
     * @see java.util.concurrent.BlockingDeque
     * @see AbstractQueue
     */
    ConcurrentLinkedDeques(ConcurrentLinkedDeque.class) {
        @Override
        public ConcurrentLinkedDeque get() { return new ConcurrentLinkedDeque(); }

        @Override
        public ConcurrentLinkedDeque apply(int value) { return new ConcurrentLinkedDeque(); }

        @Override
        public ConcurrentLinkedDeque apply(Collection collection) { return new ConcurrentLinkedDeque(collection); }
    };

    /**
     * 枚举信息
     *
     * @return
     */
    @Override
    public final String getText() { return type.getName(); }

    static final class CtorCached {

        final static HashMap<Class, Queues> CACHE = new HashMap();
    }

    private final Class type;

    Queues(Class type) {
        CtorCached.CACHE.put(this.type = type, this);
        CollectsCached.put(type, this);
    }

    @Override
    public Class type() { return type; }

    /**
     * 从集合类名获取映射实例，不存在返回 null
     *
     * @param type Queue 集合类
     *
     * @return 查找到的对象或 null
     */
    public static Queues getOrNull(Class type) { return Queues.CtorCached.CACHE.get(type); }

    /**
     * 从对象获取映射，不存在返回 null
     *
     * @param queue Queue 集合对象
     *
     * @return
     */
    public static Queues getOrNull(Object queue) { return queue == null ? null : getOrNull(queue.getClass()); }

    /**
     * 从集合类名获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param type Queue 集合类
     *
     * @return
     */
    public static Queues getAsSuperOrNull(Class type) {
        for (Queues collect; type != null; type = type.getSuperclass()) {
            if ((collect = getOrNull(type)) != null) {
                return collect;
            }
        }
        return null;
    }

    /**
     * 从对象获取映射，不存在返回 null 会追溯超类直至 Object.class 为止返回 null
     *
     * @param queue Queue 集合对象
     *
     * @return
     */
    public static Queues getAsSuperOrNull(Object queue) {
        return queue == null ? null : getAsSuperOrNull(queue.getClass());
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType
     *
     * @param type        Queue 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    public static Queues getOrDefault(Class type, Queues defaultType) {
        return Queues.CtorCached.CACHE.getOrDefault(type, defaultType);
    }

    /**
     * 从对象获取映射，不存在返回 defaultType
     *
     * @param queue       Queue 集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    public static Queues getOrDefault(Object queue, Queues defaultType) {
        return queue == null ? defaultType : getOrDefault(queue.getClass(), defaultType);
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType 此方法会一直追溯集合类的超类，直至 Object.class 为止返回 defaultType
     *
     * @param type        Queue 集合类
     * @param defaultType 默认值
     *
     * @return
     */
    public static Queues getAsSuperOrDefault(Class type, Queues defaultType) {
        for (Queues collect; type != null; type = type.getSuperclass()) {
            if ((collect = getOrNull(type)) != null) {
                return collect;
            }
        }
        return defaultType;
    }

    /**
     * 从对象获取映射，不存在返回 defaultType 此方法会一直追溯对象的超类，直至 Object.class 为止返回 defaultType
     *
     * @param queue       集合对象
     * @param defaultType 默认值
     *
     * @return
     */
    public static Queues getAsSuperOrDefault(Object queue, Queues defaultType) {
        return queue == null ? defaultType : getAsSuperOrDefault(queue.getClass(), defaultType);
    }

    /**
     * 可以自动推断
     *
     * @param type Queue 集合类
     *
     * @return
     */
    public static Queues deduce(Class<? extends Set> type) {
        return deduceOrDefault(type, LinkedLists);
    }

    /**
     * 可以自动推断
     *
     * @param setType Queue 集合类
     *
     * @return
     */
    public static Queues deduceOrDefault(Class setType, Queues type) {
        Queues collect = getAsSuperOrNull(setType);
        if (collect == null && setType != null) {
            Queues find = nullableFind(values(), item -> item.type().isAssignableFrom(setType));
            return defaultIfNull(find, type);
        }
        return collect;
    }

    /**
     * 可以自动推断
     *
     * @param queue Set 集合对象
     *
     * @return
     */
    public static Queues deduce(Object queue) {
        return deduceOrDefault(queue, LinkedLists);
    }

    /**
     * 可以自动推断
     *
     * @param queue Queue 集合对象
     *
     * @return
     */
    public static Queues deduceOrDefault(Object queue, Queues type) {
        Queues collect = getAsSuperOrNull(queue);
        if (collect == null && queue != null) {
            return defaultIfNull(nullableFind(values(), item -> item.type().isInstance(queue)), type);
        }
        return collect;
    }
}
