package com.moon.runner.core;

import com.moon.runner.core.InvokeEnsure.EnsureArgs0;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.moon.core.lang.reflect.MethodUtil.getDeclaredMethods;
import static com.moon.core.lang.reflect.MethodUtil.invoke;

/**
 * @author moonsky
 */
final class InvokeArgs0 extends InvokeAbstract {

    static abstract class BaseInvoker implements AsInvoker {

        final String methodName;

        private Method method;
        private Class declareClass;

        BaseInvoker(String methodName) {this.methodName = methodName;}

        public String getMethodName() { return methodName; }

        public Method getMethod(Object data) {
            if (declareClass == null || !declareClass.isInstance(data)) {
                Class type = Objects.requireNonNull(data).getClass();
                method = memberArgs0(type, methodName);
                declareClass = type;
            }
            return method;
        }

        @Override
        public String toString() { return method == null ? methodName : stringify(method); }
    }

    static class NonMember extends BaseInvoker {

        NonMember(String methodName) { super(methodName); }

        @Override
        public Object run(Object data) { return invoke(true, getMethod(data), data); }
    }

    enum NonDefault implements AsInvoker {
        wait,
        clone,
        notify,
        toString,
        hashCode,
        notifyAll,
        getClass;

        private final Method method;

        NonDefault() { method = filterArgs0(getDeclaredMethods(Object.class, name()), Object.class, name()); }

        @Override
        public Object run(Object data) { return invoke(true, method, data); }

        static AsInvoker get(String name) {
            try {
                return valueOf(name);
            } catch (Throwable t) {
                return null;
            }
        }

        @Override
        public String toString() { return stringify(method); }
    }

    static AsInvoker memberArgs0(String name) {
        AsInvoker invoker = NonDefault.get(name);
        return invoker == null ? new NonMember(name) : invoker;
    }

    static AsRunner memberArgs0Runner(Class type, String name, AsValuer src) {
        List<Method> ms = memberMethods(type, name);
        switch (ms.size()) {
            case 0:
                return doThrowNull();
            case 1:
                return new EnsureArgs0(ms.get(0), src);
            default:
                return new GetLink(src, memberArgs0(name));
        }
    }

    final static AsRunner parse(AsValuer prev, String name, boolean isStatic) {
        if (isStatic) {
            Class type = ((DataClass) prev).getValue();
            return ensure(staticArgs0(type, name));
        } else if (prev.isConst()) {
            // 成员方法
            Class target = prev.run().getClass();
            return memberArgs0Runner(target, name, prev);
        } else {
            return new GetLink(prev, memberArgs0(name));
        }
    }
}
