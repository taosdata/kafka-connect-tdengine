package com.moon.core.lang.ref;

import com.moon.core.lang.Executable;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.BooleanConsumer;
import com.moon.core.util.function.BooleanFunction;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public class BooleanAccessor implements BooleanSupplier {

    private boolean value;

    public BooleanAccessor() { this(false); }

    public BooleanAccessor(boolean value) { this.set(value); }

    /*
     * ----------------------------------------------------------------------------
     * static constructor
     * ----------------------------------------------------------------------------
     */

    public static BooleanAccessor ofFalse() { return of(false); }

    public static BooleanAccessor ofTrue() { return of(true); }

    public static BooleanAccessor of(boolean value) { return new BooleanAccessor(value); }

    public static BooleanAccessor of() { return ofFalse(); }

    /*
     * ----------------------------------------------------------------------------
     * sets
     * ----------------------------------------------------------------------------
     */

    public boolean get() { return value; }

    @Override
    public boolean getAsBoolean() { return get(); }

    public BooleanAccessor set(boolean value) {
        this.value = value;
        return this;
    }

    public BooleanAccessor set(BooleanSupplier supplier) { return set(supplier.getAsBoolean()); }

    public BooleanAccessor setTrue() { return set(true); }

    public BooleanAccessor setFalse() { return set(false); }

    public BooleanAccessor flip() { return set(!value); }

    /*
     * ----------------------------------------------------------------------------
     * assertions
     * ----------------------------------------------------------------------------
     */

    public boolean isTrue() { return value; }

    public boolean isFalse() { return !value; }

    /*
     * ----------------------------------------------------------------------------
     * defaultExecutor
     * ----------------------------------------------------------------------------
     */

    public BooleanAccessor ifTrue(Executable executable) {
        if (isTrue()) {
            executable.execute();
        }
        return this;
    }

    public BooleanAccessor ifFalse(Executable executable) {
        if (isFalse()) {
            executable.execute();
        }
        return this;
    }

    public BooleanAccessor ifTrue(BooleanConsumer consumer) {
        if (isTrue()) {
            consumer.accept(true);
        }
        return this;
    }

    public BooleanAccessor ifFalse(BooleanConsumer consumer) {
        if (isFalse()) {
            consumer.accept(false);
        }
        return this;
    }

    public BooleanAccessor use(BooleanConsumer consumer) {
        consumer.accept(value);
        return this;
    }

    /*
     * ----------------------------------------------------------------------------
     * apply by when
     * ----------------------------------------------------------------------------
     */

    public <T> T ifTrueOrNull(Supplier<T> supplier) { return isTrue() ? supplier.get() : null; }

    public <T> T ifFalseOrNull(Supplier<T> supplier) { return isFalse() ? supplier.get() : null; }

    public <T> T ifTrueOrThrow(Supplier<T> supplier) { return isTrue() ? supplier.get() : ThrowUtil.runtime(toString()); }

    public <T> T ifFalseOrThrow(Supplier<T> supplier) { return isFalse() ? supplier.get() : ThrowUtil.runtime(toString()); }

    public <R> R apply(BooleanFunction<R> function) { return function.apply(value); }

    @Override
    public String toString() { return Boolean.toString(value); }
}
