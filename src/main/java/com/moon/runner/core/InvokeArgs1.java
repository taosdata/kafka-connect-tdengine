package com.moon.runner.core;

import com.moon.runner.core.InvokeEnsure.EnsureArgs1;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.moon.core.lang.ClassUtil.toPrimitiveClass;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.reflect.MethodUtil.invoke;

/**
 * @author moonsky
 */
final class InvokeArgs1 extends InvokeAbstract {

    private InvokeArgs1() { noInstanceError(); }

    static class OneStaticDynamic implements AsInvoker {

        final Method srcMethod;
        final AsRunner paramValuer;
        final Set<Map.Entry<Class, Method>> METHODS_SET;

        Method currentMethod;

        OneStaticDynamic(AsRunner paramValuer, List<Method> methods) {
            final Map<Class, Method> methodsMap = new HashMap<>();
            methods.forEach(m -> methodsMap.put(m.getParameterTypes()[0], m));
            METHODS_SET = methodsMap.entrySet();
            this.paramValuer = paramValuer;
            srcMethod = methods.get(0);
        }

        private final Method withCustom(Class dataType) {
            Class currType, prevType = null;
            Method method = null, tmp;
            Set<Map.Entry<Class, Method>> methodsSet = this.METHODS_SET;
            for (Map.Entry<Class, Method> entry : methodsSet) {
                if ((currType = entry.getKey()).isAssignableFrom(dataType)) {
                    if (prevType == null || prevType.isAssignableFrom(currType)) {
                        method = entry.getValue();
                        prevType = currType;
                    }
                }
            }
            if (method == null) {
                for (Map.Entry<Class, Method> entry : methodsSet) {
                    if ((tmp = entry.getValue()).isVarArgs()) {
                        currType = entry.getKey().getComponentType();
                        if (currType.isAssignableFrom(dataType)) {
                            if (prevType == null || prevType.isAssignableFrom(currType)) {
                                prevType = currType;
                                method = tmp;
                            }
                        }
                    }
                }
            }
            return method;
        }

        private final Method withPrimitive(final Class dataType, final Class currType) {
            Class temp;
            Method method = null, tmp;
            boolean isPrimitive = false;
            Set<Map.Entry<Class, Method>> methodsSet = this.METHODS_SET;
            for (Map.Entry<Class, Method> entry : methodsSet) {
                temp = entry.getKey();
                if (temp == currType) {
                    method = entry.getValue();
                    break;
                } else if (temp == dataType) {
                    method = entry.getValue();
                }
            }
            if (method == null) {
                for (Map.Entry<Class, Method> entry : methodsSet) {
                    if ((tmp = entry.getValue()).isVarArgs()) {
                        temp = entry.getKey().getComponentType();
                        if (temp == currType) {
                            isPrimitive = true;
                            method = tmp;
                            break;
                        } else if (temp == dataType && !isPrimitive) {
                            method = tmp;
                        }
                    }
                }
            }
            return method;
        }

        private final Method withNumber(final Class dataType, Class currType) {
            Class temp;
            Method method = null, tmp;
            boolean isPrimitive = false;
            Set<Map.Entry<Class, Method>> methodsSet = this.METHODS_SET;
            final int origin = getOrdinal(currType);
            int currOrdinal = MAX_ORDINAL, ordinal;
            for (Map.Entry<Class, Method> entry : methodsSet) {
                if ((temp = entry.getKey()).isPrimitive()) {
                    ordinal = getOrdinal(temp);
                    if (ordinal >= origin && ordinal <= currOrdinal) {
                        method = entry.getValue();
                        currOrdinal = ordinal;
                        isPrimitive = true;
                    }
                } else if (dataType == temp && !isPrimitive) {
                    method = entry.getValue();
                }
            }
            if (method == null) {
                for (Map.Entry<Class, Method> entry : methodsSet) {
                    if ((tmp = entry.getValue()).isVarArgs()) {
                        temp = entry.getKey().getComponentType();
                        ordinal = getOrdinal(temp);
                        if (ordinal >= origin && ordinal <= currOrdinal) {
                            currOrdinal = ordinal;
                            isPrimitive = true;
                            method = tmp;
                        } else if (dataType == temp && !isPrimitive) {
                            method = tmp;
                        }
                    }
                }
            }
            return method;
        }

        private final Method reloadMethod(Object data) {
            Class dataType = data.getClass();
            Class currType = toPrimitiveClass(dataType);
            Method method;
            if (currType == null) {
                method = withCustom(dataType);
            } else if (currType == boolean.class || currType == char.class) {
                method = withPrimitive(dataType, currType);
            } else {
                method = withNumber(dataType, currType);
            }
            if (method == null) {
                ParseUtil.doThrow(toString());
            }
            return currentMethod = method;
        }

        Method getMethod(Object data) { return currentMethod == null ? reloadMethod(data) : currentMethod; }

        @Override
        public Object run(Object data) {
            Object param = paramValuer.run(data);
            try {
                return invoke(true, getMethod(param), null, param);
            } catch (Throwable t) {
                return invoke(true, reloadMethod(param), null, param);
            }
        }

        @Override
        public String toString() { return stringify(srcMethod); }
    }

    static AsRunner staticCall1(Class type, String name, AsRunner paramValuer) {
        List<Method> ms = filterByParamCount(staticMethods(type, name), 1);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(type, name);
            case 1:
                return ensure(ms.get(0), paramValuer);
            default:
                return new OneStaticDynamic(paramValuer, ms);
        }
    }

    static AsRunner memberCall1(Class type, String name, AsValuer src, AsRunner no1) {
        List<Method> ms = memberMethods(type, name);
        switch (ms.size()) {
            case 0:
                return doThrowNull();
            case 1:
                return new EnsureArgs1(ms.get(0), src, no1);
            default:
                return new InvokeOne(src, no1, name);
        }
    }

    final static AsRunner parse(
        AsValuer prev, String name, boolean isStatic, AsRunner no1
    ) {
        if (isStatic) {
            // 静态方法
            Class type = ((DataClass) prev).getValue();
            return staticCall1(type, name, no1);
        } else if (prev.isConst()) {
            Class target = prev.run().getClass();
            return memberCall1(target, name, prev, no1);
        } else {
            // 成员方法
            return new InvokeOne(prev, no1, name);
        }
    }
}
