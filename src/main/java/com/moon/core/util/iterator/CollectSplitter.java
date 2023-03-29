package com.moon.core.util.iterator;

import com.moon.core.enums.Collects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * 集合拆分器
 * 将集合 Tool 拆分成等 size 的 N 个相同类型集合（默认为 HashSet）
 * 最后一个不足 size 个数的统一放进最后一个集合
 * 不支持 remove 操作
 * 【隐患】
 * 如果集合是用户自定义的集合，并且同时没有空构造器和带有初始容量大小（int）型构造器
 * 这儿会默认使用 ArrayList 包装，但是在处理的时候如果任然使用自定义类型声明，会报错
 * 故建议统一用 Collection 接受，
 * 或者使用
 * {@link com.moon.core.util.IteratorUtil#split(Collection)}
 * {@link com.moon.core.util.IteratorUtil#split(Collection, int)}
 * {@link com.moon.core.util.IteratorUtil#splitter(Collection, Consumer)}
 * {@link com.moon.core.util.IteratorUtil#splitter(Collection, int, Consumer)}
 *
 * @author moonsky
 */
public class CollectSplitter<E, T extends Collection<E>> implements Iterator<T> {

    /**
     * 默认没拆分后每个集合项目个数
     */
    public final static int DEFAULT_SPLIT_COUNT = 16;
    /**
     * 默认集合类
     */
    private final Class defaultClass = ArrayList.class;
    /**
     * 源集合
     */
    private T src;
    /**
     * 源集合大小
     */
    private int size;
    /**
     * 拆分后每个集合大小
     */
    private int count;
    /**
     * 集合类是否只有空构造器
     */
    private boolean onlyEmptyConstructor = false;
    /**
     * 构造器
     */
    private Constructor<T> constructor;
    private Collects containerSupplier;
    /**
     * 容器
     */
    private Object[] container;
    /**
     * 当前位置
     */
    private int index = 0;
    /**
     * 拆分尺寸
     */
    private int length = 0;

    public CollectSplitter(T t) { this(t, DEFAULT_SPLIT_COUNT); }

    /**
     * 执行拆分
     *
     * @param t
     * @param count 指定拆分大小（拆分后每个集合元素数量）
     */
    public CollectSplitter(T t, int count) { this.split(t, count); }

    /**
     * 拆分
     *
     * @param t
     * @param count
     *
     * @return
     */
    public CollectSplitter split(T t, int count) {
        if (count < 1) {
            throw new IllegalArgumentException("after split collection size must great than 1");
        }

        this.src = t;
        this.size = size(t);
        this.count = count;

        this.init();
        this.split();
        return this;
    }

    /**
     * 初始化构造器，判断是否只有空构造器
     * 计算拆分尺寸，初始化容器
     */
    private void init() {
        if (size > 0) {
            length = size / count;
            if (size % count > 0) {
                length++;
            }
            this.container = new Object[length];

            final T src = this.src;
            Class type = src.getClass();
            if (length == 1) {
                container[0] = this.src;
            } else if ((containerSupplier = Collects.getAsSuperOrNull(type)) == null) {
                try {
                    this.constructor = type.getConstructor(int.class);
                } catch (NoSuchMethodException e) {
                    try {
                        this.constructor = type.getConstructor();
                        this.onlyEmptyConstructor = true;
                    } catch (NoSuchMethodException e1) {
                        try {
                            type = defaultClass;
                            this.constructor = type.getConstructor(int.class);
                        } catch (Exception e2) {
                            throwException(e, e1, e2);
                        }
                    }
                }
            }
        }
    }

    private void throwException(Exception... exs) {
        int len = exs.length;
        RuntimeException e;
        if (len > 0) {
            int i = 0;
            e = new IllegalArgumentException(exs[i++]);
            for (; i < len; i++) {
                e = new IllegalArgumentException(e);
            }
        } else {
            e = new IllegalArgumentException();
        }
        throw e;
    }

    /**
     * 执行拆分
     */
    private void split() {
        if (length > 1) {
            Iterator<E> i = src.iterator();
            int idx = 0;
            int counter = 0;
            T t = newCollection();
            for (; idx < size; idx++) {
                E item = i.next();

                if (counter >= count) {
                    container[index++] = t;
                    t = newCollection();
                    counter = 0;
                }

                t.add(item);
                counter++;
            }
            container[index] = t;
            index = 0;
        }
    }

    /**
     * 创建一个集合
     *
     * @return NULL of collection type
     */
    private T newCollection() {
        if (containerSupplier == null) {
            try {
                T c;
                if (onlyEmptyConstructor) {
                    c = this.constructor.newInstance();
                } else {
                    c = this.constructor.newInstance(count);
                }
                return c;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        } else {
            return (T) containerSupplier.apply(this.count);
        }
    }

    /**
     * Returns {@code true} if the iteration has more cells.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more cells
     */
    @Override
    public boolean hasNext() { return index < length; }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     *
     * @see NoSuchElementException if the iteration has no more cells
     */
    @Override
    public T next() { return (T) this.container[index++]; }

    /**
     * 工具，返回集合 size
     *
     * @param c collection
     *
     * @return size of collection
     */
    private int size(Collection c) { return c == null ? 0 : c.size(); }
}
