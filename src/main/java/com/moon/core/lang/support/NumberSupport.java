package com.moon.core.lang.support;

import com.moon.core.lang.ThrowUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author moonsky
 */
public final class NumberSupport {

    private NumberSupport() { ThrowUtil.noInstanceError(); }

    private final static Set<Class> PRIMITIVE_CLASSES = new HashSet<>();
    private final static Set<Class> WRAPPER_CLASSES = new HashSet<>();

    static {
        PRIMITIVE_CLASSES.add(byte.class);
        PRIMITIVE_CLASSES.add(short.class);
        PRIMITIVE_CLASSES.add(int.class);
        PRIMITIVE_CLASSES.add(long.class);
        PRIMITIVE_CLASSES.add(float.class);
        PRIMITIVE_CLASSES.add(double.class);

        WRAPPER_CLASSES.add(Byte.class);
        WRAPPER_CLASSES.add(Short.class);
        WRAPPER_CLASSES.add(Integer.class);
        WRAPPER_CLASSES.add(Long.class);
        WRAPPER_CLASSES.add(Float.class);
        WRAPPER_CLASSES.add(Double.class);
    }

    public static boolean isNumberPrimitiveClass(Class type) {
        return PRIMITIVE_CLASSES.contains(type);
    }

    public static boolean isNumberWrapperClass(Class type) {
        return WRAPPER_CLASSES.contains(type);
    }
}
