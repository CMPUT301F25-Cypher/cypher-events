package com.example.cypher_events;

public class Result<T> {
    public final T data;
    public final Throwable error;

    private Result(T d, Throwable e) { data = d; error = e; }

    public static <T> Result<T> ok(T d)   { return new Result<>(d, null); }
    public static <T> Result<T> err(Throwable e) { return new Result<>(null, e); }
    public boolean isOk() { return error == null; }
}