package com.moon.core.lang;

import java.lang.reflect.Modifier;

/**
 * @author moonsky
 */
public final class StackTraceUtil {

    private static Class<StackTraceUtil> TYPE = StackTraceUtil.class;

    private StackTraceUtil() {
        ThrowUtil.noInstanceError();
    }

    // ============================= methods =============================================================

    /**
     * 获取调用位置的栈信息
     *
     * @return 栈信息
     */
    public static StackTraceElement getCallerTrace() { return traces(0); }

    /**
     * 获取调用位置上一个调用方法的栈信息
     *
     * @return 上一个调用位置栈信息
     */
    public static StackTraceElement getPrevCallerTrace() {
        return traces(Mode.PREV);
    }

    /**
     * 获取调用位置上一个调用方法的类名
     *
     * @return 上一个调用方法的类名
     */
    public static String getPrevCallerTypeName() {
        return getPrevCallerTrace().getClassName();
    }

    /**
     * 获取调用位置上一个方法的方法名
     *
     * @return 上一个方法的方法名
     */
    public static String getPrevCallerMethodName() {
        return toMethodString(getPrevCallerTrace());
    }

    /**
     * 获取跳过当前调用位置所在类，上一个类相关的栈信息
     *
     * @return 上一个类相关的栈信息
     */
    public static StackTraceElement getSkipCallerTrace() {
        return traces(Mode.SKIP);
    }

    /**
     * 获取上一个类的类名
     *
     * @return 上一个类的类名
     */
    public static String getSkipCallerTypeName() {
        return getSkipCallerTrace().getClassName();
    }

    /**
     * 获取上一个类的调用者方法名
     *
     * @return 上一个类的调用者方法名
     */
    public static String getSkipCallerMethodName() {
        return toMethodString(getSkipCallerTrace());
    }

    /**
     * 获取动调用位置开始前 steps 步的栈信息
     *
     * @param steps 跳过的步数
     *
     * @return 目标栈信息
     */
    public static StackTraceElement getPrevTraceOfSteps(int steps) {
        return traces(steps);
    }

    // ============================= core ================================================================

    private static StackTraceElement traces(final Mode mode) {
        Class tempClass;
        String classname;
        Class targetClass = null;
        StackTraceElement element;
        boolean isCurrent = false;

        StackTraceElement[] traceElements = traces();
        for (int i = 3; i < traceElements.length; i++) {
            element = traceElements[i];
            classname = element.getClassName();
            if (isCurrent) {
                if (mode == Mode.SKIP &&
                    (isAbstract(tempClass = classForName(classname)) || targetClass.isAssignableFrom(tempClass))) {
                    continue;
                }
                return element;
            }
            targetClass = classForName(classname);
            if (isNotThisType(targetClass)) {
                if (mode == Mode.CALLER) {
                    return element;
                }
                isCurrent = true;
            }
        }
        return null;
    }

    private static StackTraceElement traces(int skipsCount) {
        String classname;
        StackTraceElement element;
        boolean isCurrent = false;

        StackTraceElement[] elements = traces();
        for (int i = 1, skips = skipsCount, len = elements.length; i < len; i++) {
            element = elements[i];
            classname = element.getClassName();
            if (isCurrent) {
                if (skips > 0) {
                    skips--;
                } else {
                    return element;
                }
            } else if (isNotThisType(classname)) {
                if (skips <= 0) {
                    return element;
                }
                isCurrent = true;
            } else if (i > 10) {
                return null;
            }
        }
        return null;
    }

    // ============================= tool ================================================================

    private enum Mode {
        CALLER,
        SKIP,
        PREV
    }

    private static boolean isNotThisType(Class type) {
        return !TYPE.isAssignableFrom(type);
    }

    private static boolean isNotThisType(String classname) {
        return isNotThisType(classForName(classname));
    }

    private static boolean isAbstract(Class clazz) {
        int mode = clazz.getModifiers();
        return Modifier.isAbstract(mode) || Modifier.isInterface(mode);
    }

    private static Class classForName(String name) {
        return ClassUtil.forName(name);
    }

    private static StackTraceElement[] traces() {
        return Thread.currentThread().getStackTrace();
    }

    private static String toMethodString(StackTraceElement element) {
        return toMethodString(element.getClassName(), element.getMethodName());
    }

    private static String toMethodString(String classname, String methodName) {
        return new StringBuilder(105).append(classname).append('.').append(methodName).toString();
    }
}
