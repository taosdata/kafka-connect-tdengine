package com.moon.core.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地线程计数器
 * <p>
 * (当时是遇到什么需求呢？忘了)这个类的目的是在当前线程实现一个计数
 *
 * @author moonsky
 */
public class ThreadLocalCounter {

    private final AtomicInteger total;

    private final ThreadLocal<Integer> current = new ThreadLocal<>();

    public ThreadLocalCounter() {
        this(false);
    }

    public ThreadLocalCounter(boolean counterTotal) {
        total = counterTotal ? new AtomicInteger() : null;
    }

    public int getAndAdd() {
        return getAndAdd(1);
    }

    public int getAndAdd(int amount) {
        int index = get();
        setAndIncrementTotal(index + amount);
        return index;
    }

    public int addAndGet() {
        return add();
    }

    public int add() {
        return add(1);
    }

    public int add(int amount) {
        amount += get();
        setAndIncrementTotal(amount);
        return amount;
    }

    public int get() {
        Integer currIndex = current.get();
        return currIndex == null ? 0 : currIndex.intValue();
    }

    public int total() {
        return total == null ? get() : total.get();
    }

    public int getAndRemove() {
        int index = get();
        current.remove();
        return index;
    }

    public int remove() {
        return getAndRemove();
    }

    private void setAndIncrementTotal(int value) {
        if (total != null) {
            total.incrementAndGet();
        }
        current.set(value);
    }
}
