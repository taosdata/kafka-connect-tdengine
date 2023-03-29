package com.moon.core.lang.ref;

import com.moon.core.lang.ThrowUtil;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class AccessorUtil {
    private AccessorUtil() { ThrowUtil.noInstanceError(); }

    public static <T> PhantomAccessor<T> phantom(Supplier<T> supplier) { return PhantomAccessor.of(supplier); }

    public static <T> WeakAccessor<T> weak(Supplier<T> supplier) { return WeakAccessor.of(supplier); }

    public static <T> SoftAccessor<T> soft(Supplier<T> supplier) { return SoftAccessor.of(supplier); }

    public static <T> FinalAccessor<T> ofFinal() { return FinalAccessor.of(); }

    public static <T> FinalAccessor<T> ofFinal(T value) { return FinalAccessor.of(value); }

    public static IntAccessor ofInt() { return IntAccessor.of(); }

    public static IntAccessor ofInt(int value) { return IntAccessor.of(value); }

    public static LongAccessor ofLong() { return LongAccessor.of(); }

    public static LongAccessor ofLong(long value) { return LongAccessor.of(value); }

    public static DoubleAccessor ofDouble() { return DoubleAccessor.of(); }

    public static DoubleAccessor ofDouble(double value) { return DoubleAccessor.of(value); }

    public static BooleanAccessor ofBoolean(boolean value) { return BooleanAccessor.of(value); }

    public static BooleanAccessor ofTrue() { return BooleanAccessor.ofTrue(); }

    public static BooleanAccessor ofFalse() { return BooleanAccessor.ofFalse(); }
}
