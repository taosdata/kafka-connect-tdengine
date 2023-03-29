package com.moon.core.util;

import static java.lang.String.format;

/**
 * @author moonsky
 */
public class AssertException extends RuntimeException {

    public AssertException() { }

    public AssertException(String message) { super(message); }

    public AssertException(String message, Throwable cause) { super(message, cause); }

    public AssertException(Throwable cause) { super(cause); }

    public AssertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static <T> T doThrow(String message) { throw new AssertException(message); }

    static <T> T throwNull(String message) { throw new NullPointerException(message); }

    static <T> T throwIllegal(String message) { throw new IllegalArgumentException(message); }

    static <T> T throwIllegal(String template, Object... args) { return throwIllegal(format(template, args)); }
}
