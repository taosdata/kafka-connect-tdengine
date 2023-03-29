package com.moon.core.util.function;

import java.util.Objects;

/**
 * @author moonsky
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {

    /**
     * 执行有返回值的函数，并返回结果
     *
     * @param value 参数
     *
     * @return 执行结束的值
     *
     * @throws Throwable 异常
     */
    R apply(T value) throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @param value 参数
     *
     * @return 执行结束的值
     */
    default R applyWithUnchecked(T value) {
        try {
            return apply(value);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Executor error.", t);
        }
    }

    /**
     * 返回值不能是 null
     *
     * @param value 参数
     *
     * @return 执行结束的值
     */
    default R requireApply(T value) {
        return Objects.requireNonNull(applyWithUnchecked(value));
    }
}
