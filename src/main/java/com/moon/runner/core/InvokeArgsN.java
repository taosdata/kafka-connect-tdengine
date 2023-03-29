package com.moon.runner.core;

import com.moon.runner.core.InvokeEnsure.EnsureArgsN;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author moonsky
 */
final class InvokeArgsN extends InvokeAbstract {

    static AsRunner staticCallN(Class type, String name, AsRunner... params) {
        List<Method> ms = staticMethods(type, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(type, name);
            case 1:
                return ensure(ms.get(0), params);
            default:
                return ParseUtil.doThrow("暂未支持");
        }
    }

    static AsRunner memberCallN(AsRunner prev, Class type, String name, AsRunner...params){
        List<Method> ms = memberMethods(type, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(type, name);
            case 1:
                return new EnsureArgsN(ms.get(0),prev,params);
            default:
                return ParseUtil.doThrow("暂未支持");
        }
    }

    final static AsRunner parse(
        AsValuer prev, String name, boolean isStatic, AsRunner... params
    ) {
        if (isStatic) {
            // 静态方法
            Class type = ((DataClass) prev).getValue();
            return staticCallN(type, name, params);
        } else if (prev.isConst()) {
            Class type = prev.run().getClass();
            return memberCallN(prev, type, name, params);
        } else {
            // 成员方法
            return ParseUtil.doThrow("暂未支持");
        }
    }
}
