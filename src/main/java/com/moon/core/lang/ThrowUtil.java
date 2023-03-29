package com.moon.core.lang;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class ThrowUtil {

    private ThrowUtil() { noInstanceError(); }

    /**
     * 程序运行时状态异常
     *
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T illegalState() { throw new IllegalStateException(); }

    /**
     * 程序运行时状态异常
     *
     * @param reason 异常原因
     * @param <T>    兼容返回值
     *
     * @return undefined
     */
    public static <T> T illegalState(String reason) { throw new IllegalStateException(reason); }

    /**
     * 程序运行时参数异常
     *
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T illegalArg() { throw new IllegalArgumentException(); }

    /**
     * 程序运行时参数异常
     *
     * @param reason 异常原因
     * @param <T>    兼容返回值
     *
     * @return undefined
     */
    public static <T> T illegalArg(String reason) { throw new IllegalArgumentException(reason); }

    /**
     * 程序运行时异常（状态异常）
     *
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T runtime() { return illegalState(); }

    /**
     * 程序运行时异常（状态异常）
     *
     * @param reason 异常原因
     * @param <T>    兼容返回值
     *
     * @return undefined
     */
    public static <T> T runtime(String reason) { return illegalState(reason); }

    /**
     * 程序运行时异常（默认状态异常）
     *
     * @param reason 异常类型或原因
     * @param <T>    兼容返回值
     *
     * @return undefined
     */
    public static <T> T runtime(Object reason) {
        if (reason == null) {
            throw new NullPointerException();
        } else if (reason instanceof RuntimeException) {
            throw (RuntimeException) reason;
        } else if (reason instanceof Error) {
            throw (Error) reason;
        } else if (reason instanceof Throwable) {
            throw new IllegalStateException((Throwable) reason);
        } else if (reason instanceof Supplier) {
            return runtime(((Supplier) reason).get());
        } else {
            throw new IllegalStateException(reason.toString());
        }
    }

    /**
     * 程序运行时非检查异常
     *
     * @param e   异常类型
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T unchecked(Throwable e) { return runtime(e); }

    /**
     * 程序运行时非检查异常
     *
     * @param e   异常类型
     * @param msg 异常消息
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T unchecked(Throwable e, String msg) { return runtime(e, msg); }

    /**
     * 程序运行时非检查异常
     *
     * @param e   异常类型
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T runtime(Throwable e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof Error) {
            throw (Error) e;
        } else {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 程序运行时非检查异常
     *
     * @param e   异常类型
     * @param msg 异常消息
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T runtime(Throwable e, String msg) { throw new IllegalStateException(msg, e); }

    /*
     * -----------------------------------------------------------------------
     * reject access
     * -----------------------------------------------------------------------
     */

    /**
     * 用于私有构造方法等，抛出一个错误表示该类不能有实例存在
     *
     * @see java.util.Objects
     */
    public static void noInstanceError() {
        noInstanceError("No " + StackTraceUtil.getPrevCallerTypeName() + " instances for you!");
    }

    /**
     * 用于私有构造方法等，抛出一个错误表示该类不能有实例存在
     */
    public static void noInstanceError(String message) { throw new AssertionError(message); }

    /**
     * 拒绝访问
     */
    public final static <T> T rejectAccessError() {
        return rejectAccessError("Refuse to apply. \n\tLocation: " + StackTraceUtil.getPrevTraceOfSteps(1));
    }

    /**
     * 拒绝访问
     *
     * @param message 异常信息
     */
    public final static <T> T rejectAccessError(String message) { throw new IllegalAccessError(message); }

    /**
     * 不支持的操作
     *
     * @param <T> 兼容返回值
     *
     * @return undefined
     */
    public static <T> T unsupported() { throw new UnsupportedOperationException(); }

    /**
     * 不支持的操作
     *
     * @param message 异常消息
     * @param <T>     兼容返回值
     *
     * @return undefined
     */
    public static <T> T unsupported(String message) { throw new UnsupportedOperationException(message); }
}
