package com.moon.runner.core;

import com.moon.core.lang.reflect.MethodUtil;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author moonsky
 */
abstract class InvokeBase implements AsInvoker {
    final String methodName;

    private Method method;
    private Class declaringClass;

    protected InvokeBase(String methodName) {
        Objects.requireNonNull(methodName);
        this.methodName = methodName;
    }

    public String getMethodName() { return methodName; }

    protected Method getMethod(Object data) {
        Method m = this.method;
        if (m == null || !declaringClass.isInstance(data)) {
            Objects.requireNonNull(data, methodName);
            Class type = declaringClass = data.getClass();
            this.method = m = MethodUtil.getPublicMethod(type, methodName);
        }
        return m;
    }

    private Class paramType;

    protected Method getMethod(Object data, Object one) {
        Method m = this.method;
        if (m == null || !declaringClass.isInstance(data)) {
            Objects.requireNonNull(data, methodName);
            Class type = declaringClass = data.getClass();
            this.method = m = MethodUtil.getPublicMethod(type, methodName, paramType = one.getClass());
        } else if (one == null && !paramType.isPrimitive()) {
            throw new NullPointerException(m.toString() + "[can not cast to type of (" + paramType + ")]");
        }
        return m;
    }
}
