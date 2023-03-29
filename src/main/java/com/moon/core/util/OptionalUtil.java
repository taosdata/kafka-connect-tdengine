package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.NullableFunction;

import java.util.function.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class OptionalUtil {

    private OptionalUtil() { noInstanceError(); }

    /*
     * -----------------------------------------------------------
     * return int value
     * -----------------------------------------------------------
     */

    public static <T> int computeOrZeroIfNull(T obj, ToIntFunction<T> function) {
        return computeOrDefaultIfNull(obj, function, 0);
    }

    public static <T> int computeOrOneIfNull(T obj, ToIntFunction<T> function) {
        return computeOrDefaultIfNull(obj, function, 1);
    }

    public static <T> int computeOrDefaultIfNull(T obj, ToIntFunction<T> fn, int defaultValue) {
        return obj == null ? defaultValue : fn.applyAsInt(obj);
    }

    public static <T> int computeOrElseIfNull(T obj, ToIntFunction<T> function, IntSupplier supplier) {
        return obj == null ? supplier.getAsInt() : function.applyAsInt(obj);
    }

    /*
     * -----------------------------------------------------------
     * return value
     * -----------------------------------------------------------
     */

    public static <T, R> R computeOrNull(T obj, Function<T, R> function) {
        return computeOrDefault(obj, function, null);
    }

    public static <T, R> R computeOrDefault(T obj, Function<T, R> function, R elseVal) {
        return obj == null ? elseVal : function.apply(obj);
    }

    public static <T, R> R computeOrElse(T obj, Function<T, R> function, Supplier<R> supplier) {
        return obj == null ? supplier.get() : function.apply(obj);
    }

    public static <T, R> R computeOrThrow(T obj, Function<T, R> function) {
        return obj == null ? ThrowUtil.runtime() : function.apply(obj);
    }

    public static <T, R> R computeOrThrow(T obj, Function<T, R> function, String message) {
        return obj == null ? ThrowUtil.runtime(message) : function.apply(obj);
    }

    /*
     * -----------------------------------------------------------
     * doesn't has return value
     * -----------------------------------------------------------
     */

    public static <T> void ifPresent(T obj, Consumer<T> consumer) {
        if (obj != null) {
            consumer.accept(obj);
        }
    }

    /*
     * get or else
     */

    public static <T> T orElse(T value, T defaultVal) {
        return value == null ? defaultVal : value;
    }

    public static <T> T orElseGet(T value, Supplier<T> defaultSupplier) {
        return value == null ? defaultSupplier.get() : value;
    }

    public static Object getOrNull(Object optional) {
        return resolveOrNull(optional);
    }

    @SuppressWarnings("all")
    public static Object resolveOrNull(Object optionalReference) {
        return TypeofOptional.resolveOrNull(optionalReference);
    }
}
