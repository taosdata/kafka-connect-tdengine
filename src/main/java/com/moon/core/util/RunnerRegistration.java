package com.moon.core.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class RunnerRegistration {

    private final List<Runnable> RUNNERS;

    public RunnerRegistration() { this(CopyOnWriteArrayList::new); }

    public RunnerRegistration(Supplier<List<Runnable>> creator) {
        this.RUNNERS = creator.get();
    }

    /**
     * 注册一个 runner
     *
     * @param runner
     */
    public void registry(Runnable runner) { RUNNERS.add(runner); }

    /**
     * 执行所有任务,并删除
     */
    public void runningTakeAll() { takeAll(true).forEach(Runnable::run); }

    /**
     * 执行所有任务
     */
    public void runningAll() { takeAll(false).forEach(Runnable::run); }

    /**
     * 取出所有任务
     *
     * @param clear
     *
     * @return
     */
    public synchronized List<Runnable> takeAll(boolean clear) {
        List<Runnable> runners = ListUtil.newList(RUNNERS);
        if (clear) {
            RUNNERS.clear();
        }
        return runners;
    }

    public static RunnerRegistration newInstance() { return new RunnerRegistration(); }
}
