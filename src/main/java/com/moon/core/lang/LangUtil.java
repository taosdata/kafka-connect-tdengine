package com.moon.core.lang;

import com.moon.core.util.OptionalUtil;
import com.moon.core.util.function.*;

import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.ThrowUtil.runtime;

/**
 * @author moonsky
 */
public final class LangUtil {

    private LangUtil() { noInstanceError(); }

    /**
     * 忽略检查异常执行
     *
     * @param run
     */
    public static void run(ThrowingRunnable run) {
        try {
            run.run();
        } catch (Throwable t) {
            runtime(t);
        }
    }

    /**
     * 忽略检查异常获取一个值
     *
     * @param supplier
     * @param <T>
     *
     * @return
     */
    public static <T> T get(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return runtime(t);
        }
    }

    public static <T> T getOrElse(ThrowingSupplier<T> supplier, Supplier<T> defaultSupplier) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return defaultSupplier.get();
        }
    }

    public static <T> T getOrDefault(ThrowingSupplier<T> supplier, T defaultValue) {
        try {
            return OptionalUtil.orElse(supplier.get(), defaultValue);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    /**
     * 忽略检查异常消费
     *
     * @param value
     * @param consumer
     * @param <T>
     */
    public static <T> void accept(T value, ThrowingConsumer<? super T> consumer) {
        try {
            consumer.accept(value);
        } catch (Throwable t) {
            runtime(t);
        }
    }

    /**
     * 忽略检查异常转换
     *
     * @param value
     * @param function
     * @param <T>
     * @param <R>
     *
     * @return
     */
    public static <T, R> R apply(T value, ThrowingFunction<? super T, R> function) {
        try {
            return function.apply(value);
        } catch (Throwable t) {
            return runtime(t);
        }
    }

    /**
     * 忽略检查异常转换
     *
     * @param v1
     * @param v2
     * @param function
     * @param <T>
     * @param <O>
     * @param <R>
     *
     * @return
     */
    public static <T, O, R> R applyBi(T v1, O v2, ThrowingBiFunction<? super T, ? super O, R> function) {
        try {
            return function.apply(v1, v2);
        } catch (Throwable t) {
            return runtime(t);
        }
    }
}
