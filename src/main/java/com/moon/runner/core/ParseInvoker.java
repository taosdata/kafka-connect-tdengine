package com.moon.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.reflect.FieldUtil;
import com.moon.runner.RunnerSetting;

import java.lang.reflect.Field;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.reflect.FieldUtil.getAccessibleField;
import static com.moon.runner.core.Constants.YUAN_L;
import static com.moon.runner.core.Constants.YUAN_R;
import static com.moon.runner.core.ParseUtil.nextVal;
import static java.util.Objects.requireNonNull;

/**
 * @author moonsky
 */
final class ParseInvoker {

    private ParseInvoker() { noInstanceError(); }

    final static AsRunner tryParseInvoker(
        char[] chars, IntAccessor indexer, int len, RunnerSetting settings, String methodName, AsValuer prevValuer
    ) {
        final AsRunner runner;
        final int cache = indexer.get();
        final boolean isStatic = prevValuer instanceof DataClass;
        if (nextVal(chars, indexer, len) == YUAN_L) {
            if (nextVal(chars, indexer, len) == YUAN_R) {
                // 无参方法调用
                runner = InvokeArgs0.parse(prevValuer, methodName, isStatic);
            } else {
                // 带有参数的方法调用
                runner = parseHasParams(chars, indexer.decrement(), len, settings, prevValuer, methodName, isStatic);
            }
        } else {
            // 静态字段检测
            indexer.set(cache);
            runner = tryParseStaticField(prevValuer, methodName, isStatic);
        }
        return (runner instanceof AsInvoker) ? ((AsInvoker) runner).tryToConst() : runner;
    }

    /**
     * 带有参数的方法调用
     */
    private final static AsRunner parseHasParams(
        char[] chars,
        IntAccessor indexer,
        int len,
        RunnerSetting settings,
        AsValuer prev,
        String name,
        boolean isStatic
    ) {
        AsRunner[] params = ParseParams.parse(chars, indexer, len, settings);
        switch (params.length) {
            case 0:
                return InvokeArgs0.parse(prev, name, isStatic);
            case 1:
                return InvokeArgs1.parse(prev, name, isStatic, params[0]);
            case 2:
                return InvokeArgs2.parse(prev, name, isStatic, params[0], params[1]);
            case 3:
                return InvokeArgs3.parse(prev, name, isStatic, params[0], params[1], params[2]);
            default:
                return InvokeArgsN.parse(prev, name, isStatic, params);
        }
    }

    /**
     * 尝试解析静态字段，如果不是静态字段调用返回 null
     */
    private final static AsValuer tryParseStaticField(
        AsValuer prevValuer, String name, boolean isStatic
    ) {
        if (isStatic) {
            // 静态字段
            Class sourceType = ((DataClass) prevValuer).getValue();
            Field field = requireNonNull(getAccessibleField(sourceType, name));
            return DataConst.get(FieldUtil.getValue(field, sourceType));
        }
        return null;
    }
}
