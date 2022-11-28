package com.taosdata.kafka.connect.exception;

public class SchemaException extends RuntimeException {
    public SchemaException(String s) {
        super(s);
    }

    public SchemaException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SchemaException(Throwable throwable) {
        super(throwable);
    }
}