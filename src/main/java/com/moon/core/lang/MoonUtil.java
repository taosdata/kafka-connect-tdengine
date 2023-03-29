package com.moon.core.lang;

import com.moon.core.util.CPUUtil;
import com.moon.core.util.concurrent.RejectedUtil;

import java.util.concurrent.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
@Warning
public final class MoonUtil {

    private final static ThreadPoolExecutor executor;

    private final static MoonConfig CONFIG = new MoonConfig();

    static {
        int core = 1;
        int max = CPUUtil.getCoreCount();
        long timeout = 60 * 1000;
        int queueSize = 32;
        BlockingQueue queue = new ArrayBlockingQueue(queueSize);

        executor = new ThreadPoolExecutor(core,
            max,
            timeout,
            TimeUnit.MILLISECONDS,
            queue,
            Executors.defaultThreadFactory(),
            RejectedUtil.callerRun());
        executor.allowsCoreThreadTimeOut();
    }

    private MoonUtil() { noInstanceError(); }

    public static Future<?> run(Runnable runner) {
        return executor.submit(runner);
    }

    public static <T> Future<T> run(Callable<T> runner) {
        return executor.submit(runner);
    }

    public static MoonConfig getMoonConfig() { return CONFIG; }

    public static void setMoonConfig(MoonConfig config) { CONFIG.override(config); }
}
