package com.moon.core.exception;

/**
 * @author benshaoye
 */
public class IncorrectArgumentsException extends RuntimeException {

    public IncorrectArgumentsException() { }

    public IncorrectArgumentsException(String message) { super(message); }

    public IncorrectArgumentsException(String message, Throwable cause) { super(message, cause); }

    public IncorrectArgumentsException(Throwable cause) { super(cause); }

    public IncorrectArgumentsException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }

}
