package com.moon.core.lang;

import java.lang.management.ManagementFactory;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class RuntimeUtil {

    private RuntimeUtil() { noInstanceError(); }

    public static Runtime getRuntime() { return Runtime.getRuntime(); }

    /**
     * 最大内存
     */
    public static long getMaxMemory() { return getRuntime().maxMemory(); }

    /**
     * 总内存
     */
    public static long getTotalMemory() { return getRuntime().totalMemory(); }

    /**
     * 空闲内存
     */
    public static long getFreeMemory() { return getRuntime().freeMemory(); }

    /**
     * 当前进程 ID
     *
     * @see ThreadUtil#getCurrentThreadId()
     */
    public static long getCurrentPID() {
        return Long.parseLong(StringUtil.substrBefore(ManagementFactory.getRuntimeMXBean().getName(), "@"));
    }
}
