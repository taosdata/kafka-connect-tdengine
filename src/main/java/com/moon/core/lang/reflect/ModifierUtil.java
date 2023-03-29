package com.moon.core.lang.reflect;

import com.moon.core.lang.ThrowUtil;

import java.lang.reflect.*;

/**
 * @author ZhangDongMin
 * @date 2017/5/4
 */
public final class ModifierUtil {

    private ModifierUtil() {
        ThrowUtil.noInstanceError();
    }

    public static boolean isPublic(Member m) {
        return m != null && Modifier.isPublic(m.getModifiers());
    }

    public static boolean isPublic(Class c) {
        return c != null && Modifier.isPublic(c.getModifiers());
    }

    public static boolean isPrivate(Member m) {
        return m != null && Modifier.isPrivate(m.getModifiers());
    }

    public static boolean isPrivate(Class c) {
        return c != null && Modifier.isPrivate(c.getModifiers());
    }

    public static boolean isProtected(Member m) {
        return m != null && Modifier.isProtected(m.getModifiers());
    }

    public static boolean isProtected(Class c) {
        return c != null && Modifier.isProtected(c.getModifiers());
    }

    public static boolean isStatic(Member m) {
        return m != null && Modifier.isStatic(m.getModifiers());
    }

    public static boolean isStatic(Class c) {
        return c != null && Modifier.isStatic(c.getModifiers());
    }

    public static boolean isFinal(Member m) {
        return m != null && Modifier.isFinal(m.getModifiers());
    }

    public static boolean isFinal(Class c) {
        return c != null && Modifier.isFinal(c.getModifiers());
    }

    public static boolean isSynchronized(Member m) {
        return m != null && Modifier.isSynchronized(m.getModifiers());
    }

    public static boolean isVolatile(Field m) {
        return m != null && Modifier.isVolatile(m.getModifiers());
    }

    public static boolean isTransient(Field m) {
        return m != null && Modifier.isTransient(m.getModifiers());
    }

    public static boolean isNative(Method m) {
        return m != null && Modifier.isNative(m.getModifiers());
    }

    public static boolean isInterface(Class c) {
        return c != null && Modifier.isInterface(c.getModifiers());
    }

    public static boolean isAbstract(Class m) {
        return m != null && Modifier.isAbstract(m.getModifiers());
    }

    public static boolean isAbstract(Method m) {
        return m != null && Modifier.isAbstract(m.getModifiers());
    }

    public static boolean isStrict(Member m) {
        return m != null && Modifier.isStrict(m.getModifiers());
    }

    /*
     * ------------------------------------------------------------------
     * accessible
     * ------------------------------------------------------------------
     */

    public static boolean isAccessible(AccessibleObject ao) {
        return ao.isAccessible();
    }

    public static void openAccessible(AccessibleObject ao, boolean accessible) {
        if (isAccessible(ao)) {
            uncheckedOpenAccessible(ao, accessible);
        }
    }

    public static void closeAccessible(AccessibleObject ao, boolean accessible) {
        if (isAccessible(ao)) {
            uncheckedCloseAccessible(ao, accessible);
        }
    }

    public static void uncheckedOpenAccessible(AccessibleObject ao, boolean accessible) {
        if (accessible) {
            openAccessible(ao);
        }
    }

    public static void uncheckedCloseAccessible(AccessibleObject ao, boolean accessible) {
        if (accessible) {
            closeAccessible(ao);
        }
    }

    public static void openAccessible(AccessibleObject ao) {
        ao.setAccessible(true);
    }

    public static void closeAccessible(AccessibleObject ao) {
        ao.setAccessible(false);
    }
}
