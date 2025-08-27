package com.minetty.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * A writable Future.
 */
public interface Promise<T> extends Future<T> {
    Promise<T> setSuccess(T value);
    Promise<T> setFailure(Throwable cause);
    Promise<T> trySuccess(T value);
    Promise<T> tryFailure(Throwable cause);

    T get(long timeout, TimeUnit unit) throws InterruptedException;
}

