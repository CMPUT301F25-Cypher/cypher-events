package com.example.cypher_events.util;

public class Result<T> {

    // keep these private
    private final T data;
    private final Throwable error;

    private Result(T d, Throwable e) {
        this.data = d;
        this.error = e;
    }

    public static <T> Result<T> ok(T d) {
        return new Result<>(d, null);
    }

    public static <T> Result<T> err(Throwable e) {
        return new Result<>(null, e);
    }

    public boolean isOk() {
        return error == null;
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }
}
