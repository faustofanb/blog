package com.minetty.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Minimal Future to avoid depending on JDK CompletableFuture semantics.
 */
public interface Future<T> {
    boolean isSuccess();
    boolean isDone();
    Throwable cause();
    T getNow();

    Future<T> addListener(BiConsumer<T, Throwable> listener);

    <U> Future<U> map(Function<? super T, ? extends U> mapper);

    T get(long timeout, TimeUnit unit) throws InterruptedException;
}

