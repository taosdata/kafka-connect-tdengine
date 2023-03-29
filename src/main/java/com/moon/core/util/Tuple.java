package com.moon.core.util;

import com.moon.core.util.function.BiIntConsumer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * 元组，参照 Python，同时根据 Java 特性提供点别的功能
 *
 * @author moonsky
 */
public final class Tuple<T> implements Iterable<T>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private final T[] elements;

    public Tuple(T... elements) { this.elements = elements; }

    public static <T> Tuple<T> of(T... elements) { return new Tuple<>(elements); }

    /**
     * 获取指定索引元素
     *
     * @param index 索引
     *
     * @return 索引位置所指向的元素
     *
     * @throws ArrayIndexOutOfBoundsException 当索引超出数组范围时抛出异常
     */
    public T get(int index) { return elements[index]; }

    /**
     * 宽容的获取指定索引位置的元素，支持负值，以及超出范围的索引，此时将取余后获取
     *
     * @param index
     *
     * @return 索引位置所指向的元素
     */
    public T obtain(int index) {
        final T[] elements = this.elements;
        int length = elements.length;
        if (index < 0 || index >= length) {
            index = index % length + length;
        }
        return elements[index];
    }

    /**
     * 获取元组所有元素，获取到的元素是原有元素的副本，元组一旦确定，便不可修改
     *
     * @return 元组所有元素
     */
    public T[] getElements() { return Arrays.copyOf(elements, elements.length); }

    /**
     * 遍历处理所有元素
     *
     * @param consumer
     */
    @Override
    public void forEach(Consumer<? super T> consumer) { IteratorUtil.forEach(elements, consumer); }

    /**
     * 遍历处理所有元素
     *
     * @param consumer
     */
    public void forEach(BiIntConsumer<? super T> consumer) {
        IteratorUtil.forEach(elements, consumer);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<T> iterator() { return IteratorUtil.of(elements); }

    @Override
    protected Tuple clone() { return new Tuple<>(this.elements); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tuple{");
        sb.append("elements=").append(Arrays.toString(elements));
        return sb.append('}').toString();
    }
}
