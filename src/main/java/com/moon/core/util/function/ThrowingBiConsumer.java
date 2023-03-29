package com.moon.core.util.function;

/**
 * @author moonsky
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, S> {

    /**
     * 处理数据
     *
     * @param value1 待处理数据
     * @param value2 待处理数据
     *
     * @throws Throwable 异常
     */
    void accept(T value1, S value2) throws Throwable;

    /**
     * 处理数据
     *
     * @param value1 待处理数据
     * @param value2 待处理数据
     */
    default void acceptWithUnchecked(T value1, S value2) {
        try {
            accept(value1, value2);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Executor error.", t);
        }
    }
}
