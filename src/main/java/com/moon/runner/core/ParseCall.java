package com.moon.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.runner.RunnerFunction;
import com.moon.runner.RunnerSetting;

import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.runner.core.Constants.DOT;
import static com.moon.runner.core.ParseGetter.parseVar;
import static com.moon.runner.core.ParseUtil.*;

/**
 * @author moonsky
 */
class ParseCall {

    private ParseCall() { noInstanceError(); }

    /**
     * 静态方法调用或者函数调用
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     *
     * @return
     */
    final static AsRunner parse(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings
    ) {
        AsRunner runner;
        Object runnerCache;
        int curr = nextVal(chars, indexer, len);
        if (ParseUtil.isVar(curr)) {
            // 必须是符合变量名开头的名称
            String runnerName = parseVar(chars, indexer, len, curr).toString();
            runnerCache = tryLoaderOrSimpleFn(chars, indexer, len, settings, runnerName);
            if (runnerCache == null) {
                runnerCache = ensureLoadNsFn(chars, indexer, len, settings, runnerName);
            }
            if (runnerCache instanceof RunnerFunction) {
                RunnerFunction fn = (RunnerFunction) runnerCache;
                fn = tryParseNsCaller(chars, indexer, len, settings, fn);
                runner = ParseFunc.parseFunCaller(chars, indexer, len, settings, fn);
            } else {
                runner = (AsRunner) runnerCache;
            }
        } else {
            runner = throwErr(chars, indexer);
            // 为更多符号留位置，比如动态变化的类，等
            // 稍微点复杂，目前不打算实现
        }
        return Objects.requireNonNull(runner);
    }

    /**
     * 尝试解析具有命名空间的函数
     * <p>
     * 最开始解析出来的是函数，这一步仍然可能是函数
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @param fn
     *
     * @return
     */
    private final static RunnerFunction tryParseNsCaller(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, RunnerFunction fn
    ) {
        int curr = nextVal(chars, indexer, len);
        if (curr == DOT) {
            curr = nextVal(chars, indexer, len);
            assertTrue(ParseUtil.isVar(curr), chars, indexer);
            String name = ParseGetter.parseVar(chars, indexer, len, curr).toString();
            final String funcName = IGetFun.toName(fn.functionName(), name);
            Object caller = ParseSetting.getFunction(settings, funcName);
            // if (settings != null) {
            //     caller = settings.getCaller(funcName);
            // }
            // if (caller == null) {
            //     caller = IGetFun.tryLoad(funcName);
            // }
            // Objects.requireNonNull(caller);
            assertTrue(caller instanceof RunnerFunction, chars, indexer);
            return (RunnerFunction) caller;
        }
        indexer.decrement();
        return fn;
    }

    /**
     * 在{@link #tryLoaderOrSimpleFn(char[], IntAccessor, int, RunnerSetting, String)}
     * 加载失败后，这里解析具有命名空间的函数
     * 同样，settingWith 优先于内置函数
     * <p>
     * 这一步解析不出来一定会抛出异常
     * <p>
     * 在尝试加载简单函数或静态方法失败后
     * 到这儿只能加载含有命名空间的函数
     * 否则抛出异常
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @param runnerName
     *
     * @return
     */
    private final static Object ensureLoadNsFn(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, String runnerName
    ) {
        // Object callerTemp = null;
        int curr = nextVal(chars, indexer, len);
        if (curr == Constants.DOT) {
            curr = nextVal(chars, indexer, len);
            String name = parseVar(chars, indexer, len, curr).toString();
            name = IGetFun.toName(runnerName, name);
            Object callerTemp = ParseSetting.getFunction(settings, name);
            // if (settings != null) {
            //     callerTemp = settings.getCaller(name);
            // }
            // if (callerTemp == null) {
            //     callerTemp = IGetFun.tryLoad(name);
            // }
            assertTrue(callerTemp instanceof RunnerFunction, chars, indexer);
            return callerTemp;
        }
        return throwErr(chars, indexer);
    }

    /**
     * 尝试解析函数或静态方法类
     * <p>
     * settingWith 优先于默认，函数优先于静态方法
     * 优先从 settingWith 加载函数或静态方法
     * 不能加载情况下尝试加载内置函数或方法
     * 这一步如果是加载函数的话，只加载简单函数，即不含有命名空间的函数
     * <p>
     * 均加载失败返回 null，失败的意思是一定是一个具有命名空间的函数，否则就是异常
     * <p>
     * 加载异常将抛出
     *
     * @param chars
     * @param indexer
     * @param len
     * @param settings
     * @param runnerName
     *
     * @return
     */
    private final static Object tryLoaderOrSimpleFn(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, String runnerName
    ) {
        Object fn = ParseSetting.getFunction(settings, runnerName);
        if (fn == null) {
            fn = ParseSetting.getCaller(settings, runnerName);
            return fn == null ? throwErr(runnerName, chars, indexer) : new DataClass((Class) fn);
        }
        return fn;
        // if (settings == null) {
        //     return tryLoadDefault(runnerName);
        // }
        // Object caller = settings.getCaller(runnerName);
        // if (caller == null) {
        //     return tryLoadDefault(runnerName);
        // } else if (caller instanceof RunnerFunction) {
        //     return caller;
        // } else if (caller instanceof Class) {
        //     return new DataClass((Class) caller);
        // }
        // return throwErr(chars, indexer);
    }

    // /**
    //  * 加载内置函数或静态方法类
    //  *
    //  * @param runnerName
    //  *
    //  * @return
    //  */
    // private static Object tryLoadDefault(String runnerName) {
    //     Object caller = IGetFun.tryLoad(runnerName);
    //     if (caller != null) {
    //         return caller;
    //     }
    //     caller = IGetLoad.tryLoad(runnerName);
    //     if (caller != null) {
    //         return new DataClass((Class) caller);
    //     }
    //     return null;
    // }
}
