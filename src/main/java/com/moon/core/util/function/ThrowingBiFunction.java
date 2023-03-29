package com.moon.core.util.function;

import java.util.Objects;

/**
 * @author moonsky
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, O, R> {

    /**
     * 处理两个数据，并返回结果
     *
     * @param value1 参数
     * @param value2 参数
     *
     * @return 返回值
     *
     * @throws Throwable 异常
     */
    R apply(T value1, O value2) throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @param value1 参数
     * @param value2 参数
     *
     * @return 返回值
     */
    default R applyWithUnchecked(T value1, O value2) {
        try {
            return apply(value1, value2);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Executor error.", t);
        }
    }

    /**
     * 返回值不能是 null
     *
     * @param value1 参数
     * @param value2 参数
     *
     * @return 执行结束的值
     */
    default R requireApply(T value1, O value2) {
        return Objects.requireNonNull(applyWithUnchecked(value1, value2));
    }
}
