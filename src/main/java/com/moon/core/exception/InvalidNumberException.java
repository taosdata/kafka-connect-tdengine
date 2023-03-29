package com.moon.core.exception;

import static java.lang.String.format;

/**
 * @author moonsky
 */
public class InvalidNumberException extends RuntimeException {

    public InvalidNumberException() { }

    public InvalidNumberException(String message) { super(message); }

    public InvalidNumberException(String message, Exception e) { super(message, e); }

    /*
     * int
     */

    public InvalidNumberException(int value) { this(format("value: %d", value)); }

    public InvalidNumberException(int value, String message) {
        this(format("value: %d; message: %s", value, message));
    }

    public InvalidNumberException(int value, String message, Exception e) {
        this(format("value: %d; message: %s", value, message), e);
    }

    public InvalidNumberException(int value, Exception e) { this(format("value: %d", value), e); }

    /*
     * long
     */

    public InvalidNumberException(long value) { this(format("value: %d", value)); }

    public InvalidNumberException(long value, String message) {
        this(format("value: %d; message: %s", value, message));
    }

    public InvalidNumberException(long value, String message, Exception e) {
        this(format("value: %d; message: %s", value, message), e);
    }

    public InvalidNumberException(long value, Exception e) { this(format("value: %d", value), e); }

    /*
     * double
     */

    public InvalidNumberException(double value) { this(format("value: %d", value)); }

    public InvalidNumberException(double value, String message) {
        this(format("value: %d; message: %s", value, message));
    }

    public InvalidNumberException(double value, String message, Exception e) {
        this(format("value: %d; message: %s", value, message), e);
    }

    public InvalidNumberException(double value, Exception e) { this(format("value: %d", value), e); }
}
