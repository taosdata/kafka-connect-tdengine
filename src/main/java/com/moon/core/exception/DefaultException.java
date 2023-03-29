package com.moon.core.exception;

/**
 * @author moonsky
 */
public class DefaultException extends RuntimeException {

    private int code = 0;

    public DefaultException() { super(); }

    /*
     * reason constructor
     */

    public DefaultException(String message) { super(message); }

    public DefaultException(Object reason) {
        this(reason instanceof Throwable ? ((Throwable) reason).getMessage() : toStr(reason));
        if (reason instanceof Throwable && reason != this) {
            this.initCause((Throwable) reason);
        }
    }

    /*
     * code and reason constructor
     */

    public DefaultException(int code, String message) {
        this(message);
        this.setCode(code);
    }

    public DefaultException(int code, Object reason) {
        this(reason);
        this.setCode(code);
    }

    /*
     * reason and throwable constructor
     */

    public DefaultException(String message, Throwable cause) { super(message, cause); }

    public DefaultException(Object reason, Throwable cause) { this(toStr(reason), cause); }

    /*
     * code, reason and throwable constructor
     */

    public DefaultException(int code, String message, Throwable cause) {
        this(message, cause);
        this.setCode(code);
    }

    public DefaultException(int code, Object reason, Throwable cause) {
        this(reason, cause);
        this.setCode(code);
    }

    /*
     * throwable constructor
     */

    public DefaultException(Throwable cause) { this(cause.getMessage(), cause); }

    public DefaultException(int code, Throwable cause) {
        this(cause.getMessage(), cause);
        this.setCode(code);
    }

    public DefaultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DefaultException(Object reason, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(toStr(reason), cause, enableSuppression, writableStackTrace);
    }

    /*
     * member method
     */

    DefaultException setCode(int code) {
        this.code = code;
        return this;
    }

    public int getCode() { return code; }

    /*
     * tools
     */

    private final static String toStr(Object reason) { return reason == null ? null : reason.toString(); }

    /*
     * static caller
     */

    public static final DefaultException with(Object reason) { return new DefaultException(reason); }

    public static final DefaultException with(int code, Object reason) { return new DefaultException(code, reason); }

    public static final DefaultException with(int code, Object reason, Throwable throwable) {
        return new DefaultException(code, reason, throwable);
    }

    public static final <T> T doThrow(Object reason) { throw with(reason); }

    public static final <T> T doThrow(int code, Object reason) { throw with(code, reason); }

    public static final <T> T doThrow(int code, Object reason, Throwable throwable) { throw with(code, reason, throwable); }
}
