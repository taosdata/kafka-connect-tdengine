package com.moon.runner.core;

import com.moon.runner.core.InvokeEnsure.EnsureArgs2;

import java.lang.reflect.Method;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
final class InvokeArgs2 extends InvokeAbstract {

    private InvokeArgs2() { noInstanceError(); }

    static AsRunner staticCall2(Class source, String name, AsRunner no1, AsRunner no2) {
        List<Method> ms = staticMethods(source, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(source, name);
            case 1:
                return ensure(ms.get(0), no1, no2);
            default:
                return ParseUtil.doThrow("暂未支持");
        }
    }

    static AsRunner memberCall2(AsValuer prev, Class source, String name, AsRunner no1, AsRunner no2) {
        List<Method> ms = memberMethods(source, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(source, name);
            case 1:
                return new EnsureArgs2(ms.get(0), prev, no1, no2);
            default:
                return ParseUtil.doThrow("暂未支持");
        }
    }

    final static AsRunner parse(
        AsValuer prev, String name, boolean isStatic, AsRunner no1, AsRunner no2
    ) {
        if (isStatic) {
            // 静态方法
            Class type = ((DataClass) prev).getValue();
            return staticCall2(type, name, no1, no2);
        } else if (prev.isConst()) {
            Class type = prev.run().getClass();
            return memberCall2(prev, type, name, no1, no2);
        } else {
            // 成员方法
            return ParseUtil.doThrow("暂未支持");
        }
    }
}
