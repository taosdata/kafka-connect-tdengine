package com.moon.core.util.function;

import java.util.Objects;

/**
 * @author moonsky
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

    /**
     * 获取值
     *
     * @return 返回执行完的值
     *
     * @throws Throwable 执行过程中的异常
     */
    T get() throws Throwable;

    /**
     * 获取值并返回，如果异常，将包装成非检查异常抛出
     *
     * @return 返回执行完的值
     */
    default T uncheckedGet() {
        try {
            return get();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Executor error.", t);
        }
    }

    /**
     * 返回值不能是 null
     *
     * @return 返回执行完的值，不能是 null
     */
    default T requireGet() { return Objects.requireNonNull(uncheckedGet()); }
}
