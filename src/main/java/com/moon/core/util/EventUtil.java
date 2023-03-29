package com.moon.core.util;

import com.moon.core.util.function.ThrowingRunnable;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class EventUtil {

    private EventUtil() { noInstanceError(); }

    /**
     * 为{@code true}时触发执行
     *
     * @param trigger 触发器
     * @param runner  执行代码
     */
    public static void on(Boolean trigger, ThrowingRunnable runner) {
        on(Boolean.TRUE.equals(trigger), runner);
    }

    /**
     * 为{@code true}时触发执行
     *
     * @param trigger 触发器
     * @param runner  执行代码
     */
    public static void on(BooleanSupplier trigger, ThrowingRunnable runner) {
        on(trigger.getAsBoolean(), runner);
    }

    /**
     * 为{@code true}时触发执行
     *
     * @param trigger 触发器
     * @param runner  执行代码
     */
    public static void on(boolean trigger, ThrowingRunnable runner) {
        if (trigger) {
            runner.uncheckedRun();
        }
    }
}
