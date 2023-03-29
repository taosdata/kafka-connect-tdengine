package com.moon.runner.core;

import com.moon.core.lang.ThrowUtil;
import com.moon.runner.core.InvokeEnsure.EnsureArgs3;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author moonsky
 */
class InvokeArgs3 extends InvokeAbstract {

    private InvokeArgs3() { ThrowUtil.noInstanceError(); }

    static AsRunner staticCall3(Class source, String name, AsRunner no1, AsRunner no2, AsRunner no3) {
        List<Method> ms = staticMethods(source, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(source, name);
            case 1:
                return ensure(ms.get(0), no1, no2, no3);
            default:
                return ParseUtil.doThrow("暂未支持");
        }
    }

    static AsRunner memberCall3(AsValuer prev, Class source, String name, AsRunner no1, AsRunner no2, AsRunner no3) {
        List<Method> ms = memberMethods(source, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(source, name);
            case 1:
                return new EnsureArgs3(ms.get(0), prev, no1, no2, no3);
            default:
                return ParseUtil.doThrow("暂未支持");
        }
    }

    final static AsRunner parse(
        AsValuer prev, String name, boolean isStatic, AsRunner no1, AsRunner no2, AsRunner no3
    ) {
        if (isStatic) {
            // 静态方法
            Class type = ((DataClass) prev).getValue();
            return staticCall3(type, name, no1, no2, no3);
        } else if (prev.isConst()) {
            Class type = prev.run().getClass();
            return memberCall3(prev, type, name, no1, no2, no3);
        } else {
            // 成员方法
            return new InvokeOne(prev, no1, name);
        }
    }
}
