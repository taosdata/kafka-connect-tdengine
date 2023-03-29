package com.moon.core.lang.ref;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

/**
 * @author moonsky
 */
public class IntAccessor extends Number implements IntSupplier {

    private int value;

    public IntAccessor() { }

    @Override
    public int intValue() { return value; }

    @Override
    public long longValue() { return value; }

    @Override
    public float floatValue() { return value; }

    @Override
    public double doubleValue() { return value; }

    public IntAccessor(int value) { this.set(value); }

    public static IntAccessor of() { return new IntAccessor(); }

    public static IntAccessor of(int value) { return new IntAccessor(value); }

    public IntAccessor set(int value) {
        this.value = value;
        return this;
    }

    /*
     * ------------------------------------------------------------
     * adds
     * ------------------------------------------------------------
     */

    public IntAccessor increment(int value) {
        this.value += value;
        return this;
    }

    public IntAccessor increment() { return increment(1); }

    public IntAccessor decrement(int value) {
        this.value -= value;
        return this;
    }

    public IntAccessor decrement() { return decrement(1); }

    /*
     * ------------------------------------------------------------
     * gets
     * ------------------------------------------------------------
     */

    public int get() { return value; }

    @Override
    public int getAsInt() { return get(); }

    public int getAndIncrement() { return value++; }

    public int incrementAndGet() { return ++value; }

    public int getAndIncrement(int value) {
        int now = this.value;
        increment(value);
        return now;
    }

    public int incrementAndGet(int value) { return this.value += value; }

    public int getAndDecrement() { return value--; }

    public int decrementAndGet() { return --value; }

    public int decrementAndGet(int value) { return this.value -= value; }

    public int getAndDecrement(int value) {
        int num = get();
        decrement(value);
        return num;
    }

    /*
     * ------------------------------------------------------------
     * assertions
     * ------------------------------------------------------------
     */

    public boolean isEq(int value) { return this.value == value; }

    public boolean isGt(int value) { return this.value > value; }

    public boolean isLt(int value) { return this.value < value; }

    public boolean isGtOrEq(int value) { return this.value >= value; }

    public boolean isLtOrEq(int value) { return this.value <= value; }

    /*
     * ------------------------------------------------------------
     * operations
     * ------------------------------------------------------------
     */

    public IntAccessor ifEq(int value, IntConsumer consumer) {
        if (isEq(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public IntAccessor ifGt(int value, IntConsumer consumer) {
        if (isGt(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public IntAccessor ifLt(int value, IntConsumer consumer) {
        if (isLt(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public IntAccessor ifGtOrEq(int value, IntConsumer consumer) {
        if (isGtOrEq(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public IntAccessor ifLtOrEq(int value, IntConsumer consumer) {
        if (isLtOrEq(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    /*
     * ------------------------------------------------------------
     * consumers
     * ------------------------------------------------------------
     */

    public IntAccessor use(IntConsumer consumer) {
        consumer.accept(value);
        return this;
    }

    public IntAccessor compute(ToIntFunction<Integer> computer) { return set(computer.applyAsInt(value)); }

    public <T> T transform(IntFunction<T> transformer) { return transformer.apply(value); }

    @Override
    public String toString() { return String.valueOf(value); }
}
