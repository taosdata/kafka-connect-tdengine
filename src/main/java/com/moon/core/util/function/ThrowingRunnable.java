package com.moon.core.util.function;

/**
 * 区别{@link Runnable}}
 *
 * @author moonsky
 */
@FunctionalInterface
public interface ThrowingRunnable {

    /**
     * 运行
     *
     * @throws Throwable 异常
     */
    void run() throws Throwable;

    /**
     * 执行并返回，如果异常，将包装成非检查异常抛出
     */
    default void uncheckedRun() {
        try {
            run();
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Executor error.", t);
        }
    }
}
