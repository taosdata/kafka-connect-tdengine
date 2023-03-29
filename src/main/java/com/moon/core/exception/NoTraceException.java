package com.moon.core.exception;

/**
 * 不打印堆栈信息的异常类
 * <p>
 * 通常异常都会收集堆栈信息，这一步比较耗费资源
 * <p>
 * 实际中也有不少场景不需要堆栈信息，此时可用这种方式实现
 *
 * @author moonsky
 */
public class NoTraceException extends Exception {

    public NoTraceException() { this(null, null, true, false); }

    public NoTraceException(String message) {
        this(message, null, true, false);
    }

    public NoTraceException(String message, Throwable cause) {
        this(message, cause, true, false);
    }

    public NoTraceException(Throwable cause) { this(cause.getMessage(), cause); }

    public NoTraceException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }
}
