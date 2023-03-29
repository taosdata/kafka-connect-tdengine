package com.moon.core.lang.ref;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

/**
 * @author moonsky
 */
public class DoubleAccessor extends Number implements DoubleSupplier {

    private double value;

    public DoubleAccessor() { }

    @Override
    public int intValue() { return (int) value; }

    @Override
    public long longValue() { return (long) value; }

    @Override
    public float floatValue() { return (float) value; }

    @Override
    public double doubleValue() { return value; }

    public DoubleAccessor(double value) { this.set(value); }

    public static DoubleAccessor of() { return new DoubleAccessor(); }

    public static DoubleAccessor of(double value) { return new DoubleAccessor(value); }

    public DoubleAccessor set(double value) {
        this.value = value;
        return this;
    }

    /*
     * ------------------------------------------------------------
     * adds
     * ------------------------------------------------------------
     */

    public DoubleAccessor increment(double value) {
        this.value += value;
        return this;
    }

    public DoubleAccessor increment() { return increment(1); }

    public DoubleAccessor decrement(double value) {
        this.value -= value;
        return this;
    }

    public DoubleAccessor decrement() { return decrement(1); }

    /*
     * ------------------------------------------------------------
     * gets
     * ------------------------------------------------------------
     */

    public double get() { return value; }

    @Override
    public double getAsDouble() { return get(); }

    public double getAndIncrement() { return value++; }

    public double incrementAndGet() { return ++value; }

    public double getAndIncrement(double value) { return this.value += value; }

    public double incrementAndGet(double value) {
        double now = this.value;
        increment(value);
        return now;
    }

    public double getAndDecrement() { return value--; }

    public double decrementAndGet() { return --value; }

    public double decrementAndGet(double value) { return this.value -= value; }

    public double getAndDecrement(double value) {
        double num = get();
        decrement(value);
        return num;
    }

    /*
     * ------------------------------------------------------------
     * assertions
     * ------------------------------------------------------------
     */

    public boolean isEq(double value) { return this.value == value; }

    public boolean isGt(double value) { return this.value > value; }

    public boolean isLt(double value) { return this.value < value; }

    public boolean isGtOrEq(double value) { return this.value >= value; }

    public boolean isLtOrEq(double value) { return this.value <= value; }

    /*
     * ------------------------------------------------------------
     * operations
     * ------------------------------------------------------------
     */

    public DoubleAccessor ifEq(double value, DoubleConsumer consumer) {
        if (isEq(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public DoubleAccessor ifGt(double value, DoubleConsumer consumer) {
        if (isGt(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public DoubleAccessor ifLt(double value, DoubleConsumer consumer) {
        if (isLt(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public DoubleAccessor ifGtOrEq(double value, DoubleConsumer consumer) {
        if (isGtOrEq(value)) {
            consumer.accept(this.value);
        }
        return this;
    }

    public DoubleAccessor ifLtOrEq(double value, DoubleConsumer consumer) {
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

    public DoubleAccessor use(DoubleConsumer consumer) {
        consumer.accept(value);
        return this;
    }

    public DoubleAccessor compute(ToDoubleFunction<Double> computer) { return set(computer.applyAsDouble(value)); }

    public <T> T transform(DoubleFunction<T> transformer) { return transformer.apply(value); }

    @Override
    public String toString() { return String.valueOf(value); }
}
