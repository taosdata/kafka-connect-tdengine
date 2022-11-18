package com.taosdata.kafka.connect.sink;

public class RecordException extends RuntimeException {
    public RecordException(String s) {
        super(s);
    }

    public RecordException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RecordException(Throwable throwable) {
        super(throwable);
    }
}