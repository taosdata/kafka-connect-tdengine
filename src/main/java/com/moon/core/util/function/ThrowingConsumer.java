package com.moon.core.util.function;

/**
 * @author moonsky
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingConsumer<T> {

    /**
     * 处理数据
     *
     * @param value 待处理数据
     *
     * @throws Throwable 异常
     */
    void accept(T value) throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @param value 待处理数据
     */
    default void acceptWithUnchecked(T value) {
        try {
            accept(value);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Executor error.", t);
        }
    }
}
