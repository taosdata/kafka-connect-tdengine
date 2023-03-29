package com.moon.core.util;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class CPUUtil {

    private CPUUtil() {
        noInstanceError();
    }

    /**
     * 核心线程数
     *
     * @return
     */
    public static int getCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
