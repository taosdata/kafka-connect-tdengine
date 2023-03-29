package com.moon.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.runner.RunnerFunction;
import com.moon.runner.RunnerSetting;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.runner.core.Constants.YUAN_L;
import static com.moon.runner.core.ParseUtil.assertTrue;
import static com.moon.runner.core.ParseUtil.nextVal;

/**
 * @author moonsky
 */
final class ParseFunc {

    private ParseFunc() { noInstanceError(); }

    final static AsRunner parseFunCaller(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, RunnerFunction fn
    ) {
        int curr = nextVal(chars, indexer, len);
        assertTrue(curr == YUAN_L, chars, indexer);
        AsRunner[] runs = ParseParams.parse(chars, indexer, len, settings);
        switch (runs.length) {
            case 0:
                return f0(fn);
            case 1:
                return f1(fn, runs[0]);
            case 2:
                return f2(fn, runs[0], runs[1]);
            case 3:
                return f3(fn, runs[0], runs[1], runs[2]);
            default:
                return fn(fn, runs);
        }
    }

    private final static AsRunner fn(RunnerFunction fn, AsRunner[] runners) {
        if (isAllConst(runners) && fn.isChangeless()) {
            Object[] values = new Object[runners.length];
            for (int i = 0; i < runners.length; i++) {
                values[i] = runners[i].run();
            }
            return DataConst.get(fn.apply(values));
        }
        return new IFunc.Multi(fn, runners);
    }

    private final static AsRunner f3(
        RunnerFunction fn, AsRunner runner, AsRunner runner0, AsRunner runner1
    ) {
        if (isAllConst(runner, runner0, runner1) && fn.isChangeless()) {
            return DataConst.get(fn.apply(runner.run(), runner0.run(), runner1.run()));
        }
        return new IFunc.Three(fn, runner, runner0, runner1);
    }

    private final static AsRunner f2(RunnerFunction fn, AsRunner runner, AsRunner runner0) {
        if (isAllConst(runner, runner0) && fn.isChangeless()) {
            return DataConst.get(fn.apply(runner.run(), runner0.run()));
        }
        return new IFunc.Two(fn, runner, runner0);
    }

    private final static AsRunner f1(RunnerFunction fn, AsRunner runner) {
        if (isAllConst(runner) && fn.isChangeless()) {
            return DataConst.get(fn.apply(runner.run()));
        }
        return new IFunc.One(fn, runner);
    }

    private final static AsRunner f0(RunnerFunction fn) {
        return fn.isChangeless() ? DataConst.get(fn.apply()) : new IFunc.Non(fn);
    }

    private final static boolean isAllConst(AsRunner... runners) {
        if (runners != null) {
            for (AsRunner runner : runners) {
                if (!runner.isConst()) {
                    return false;
                }
            }
        }
        return true;
    }
}
