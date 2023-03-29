package com.moon.core.util.concurrent;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class RejectedUtil {

    private RejectedUtil() { noInstanceError(); }

    /**
     * 拒绝并丢弃
     *
     * @return RejectedExecutionHandler
     */
    public static RejectedExecutionHandler abort() { return new AbortPolicy(); }

    /**
     * 调用方自己运行
     *
     * @return RejectedExecutionHandler
     */
    public static RejectedExecutionHandler callerRun() { return new CallerRunsPolicy(); }

    /**
     * 丢弃最老任务
     *
     * @return RejectedExecutionHandler
     */
    public static RejectedExecutionHandler discardOldest() { return new DiscardOldestPolicy(); }

    /**
     * 丢弃
     *
     * @return RejectedExecutionHandler
     */
    public static RejectedExecutionHandler discard() { return new DiscardPolicy(); }
}
