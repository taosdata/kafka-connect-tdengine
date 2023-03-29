package com.moon.runner.core;

import com.moon.core.enums.Arrays2;
import com.moon.core.lang.ThrowUtil;

import java.lang.reflect.Method;

import static com.moon.core.lang.reflect.MethodUtil.invoke;
import static com.moon.runner.core.DataNull.NULL;

/**
 * @author moonsky
 */
class InvokeDynamic {

    private InvokeDynamic() { ThrowUtil.noInstanceError(); }

    protected final static Object[] EMPTY_OBJECTS = Arrays2.OBJECTS.empty();

    static abstract class BaseDynamic implements AsInvoker {

        final Method[] ms;
        final AsValuer src;

        Method method;

        protected BaseDynamic(Method[] ms, AsValuer src) {
            this.src = src == null ? NULL : src;
            this.ms = ms;
        }

        Object[] getParams(Object data) { return EMPTY_OBJECTS; }

        public Method getMethod(Object data) { return method == null ? null : method; }

        @Override
        public final boolean isStaticInvoker() { return src == NULL; }

        @Override
        public final boolean isMemberInvoker() { return src != NULL; }

        @Override
        public final Object run(Object data) {
            Object[] params = getParams(data);
            return invoke(true, getMethod(data), src.run(data), params);
        }
    }
}
