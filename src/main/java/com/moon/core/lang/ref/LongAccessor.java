package com.moon.core.lang.ref;

import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;

/**
 * @author moonsky
 */
public class LongAccessor extends Number implements LongSupplier {

    private long value;

    public LongAccessor() { }

    @Override
    public int intValue() { return (int) value; }

    @Override
    public long longValue() { return value; }

    @Override
    public float floatValue() { return value; }

    @Override
    public double doubleValue() { return value; }

    public LongAccessor(long value) { this.set(value); }

    public static LongAccessor of() { return new LongAccessor(); }

    public static LongAccessor of(long value) { return new LongAccessor(value); }

    public LongAccessor set(long value) {
        this.value = value;
        return this;
    }

    /*
     * ------------------------------------------------------------
     * adds
     * ------------------------------------------------------------
     */

    public LongAccessor increment(long value) {
        this.value += value;
        return this;
    }

    public LongAccessor increment() { return increment(1); }

    public LongAccessor decrement(long value) {
        this.value -= value;
        return this;
    }

    public LongAccessor decrement() { return decrement(1); }

    /*
     * ------------------------------------------------------------
     * gets
     * ------------------------------------------------------------
     */

    public long get() { return value; }

    @Override
    public long getAsLong() { return get(); }

    public long getAndIncrement() { return value++; }

    public long incrementAndGet() { return ++value; }

    public long getAndIncrement(long value) { return this.value += value; }

    public long incrementAndGet(long value) {
        long now = this.value;
        increment(value);
        return now;
    }

    public long getAndDecrement() { return value--; }

    public long decrementAndGet() { return --value; }

    public long decrementAndGet(long value) { return this.value -= value; }

    public long getAndDecrement(long value) {
        long num = get();
        decrement(value);
        return num;
    }

    /*
     * ------------------------------------------------------------
     * assertions
     * ------------------------------------------------------------
     */

    public boolean isEq(long value) { return this.value == value; }

    public boolean isGt(long value) { return this.value > value; }

    public boolean isLt(long value) { return this.value < value; }

    public boolean isGtOrEq(long value) { return this.value >= value; }

    public boolean isLtOrEq(long value) { return this.value <= value; }

    /*
     * ------------------------------------------------------------
     * operations
     * ------------------------------------------------------------
     */

    public LongAccessor ifEq(long value, LongConsumer consumer) {
        if (isEq(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public LongAccessor ifGt(long value, LongConsumer consumer) {
        if (isGt(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public LongAccessor ifLt(long value, LongConsumer consumer) {
        if (isLt(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public LongAccessor ifGtOrEq(long value, LongConsumer consumer) {
        if (isGtOrEq(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public LongAccessor ifLtOrEq(long value, LongConsumer consumer) {
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

    public LongAccessor use(LongConsumer consumer) {
        consumer.accept(value);
        return this;
    }

    public LongAccessor compute(ToLongFunction<Long> computer) { return set(computer.applyAsLong(value)); }

    public <T> T transform(LongFunction<T> transformer) { return transformer.apply(value); }

    @Override
    public String toString() { return String.valueOf(value); }
}
