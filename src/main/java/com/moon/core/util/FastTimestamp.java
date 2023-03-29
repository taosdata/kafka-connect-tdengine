package com.moon.core.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * 并发场景下获取当前毫秒数，单独用一个线程不断获取并保存毫秒数
 *
 * @author moonsky
 */
public final class FastTimestamp implements LongSupplier {

    public final static FastTimestamp INSTANCE = new FastTimestamp();

    private FastTimestamp() { }

    public static FastTimestamp getInstance() { return INSTANCE; }

    public static long currentTimeMillis() { return FastTimestampRunner.TIMER.value; }

    /**
     * 可用来调整精度
     *
     * @param microseconds
     */
    public static void setPeriod(long microseconds) {
        FastTimestampRunner.TIMER.setPeriod(microseconds, TimeUnit.MICROSECONDS);
    }

    @Override
    public long getAsLong() { return currentTimeMillis(); }

    private enum FastTimestampRunner {
        /**
         * 单例计数器
         */
        TIMER;

        private final ScheduledExecutorService scheduler;
        private volatile ScheduledFuture scheduledFuture;
        private volatile long value = System.currentTimeMillis();

        @SuppressWarnings("all")
        FastTimestampRunner() {
            this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "MoonUtil-FastTimestampRunner");
                thread.setDaemon(true);
                return thread;
            });
            setPeriod(1, TimeUnit.MICROSECONDS);
        }

        private void setPeriod(long period, TimeUnit unit) {
            ScheduledFuture future = this.scheduledFuture;

            this.scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
                this.value = System.currentTimeMillis();
            }, period, period, unit);

            try {
                if (future != null && !future.isCancelled()) {
                    future.cancel(false);
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
