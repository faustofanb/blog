package com.minetty.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Very small promise implementation suitable for single-completion semantics.
 */
public final class DefaultPromise<T> implements Promise<T> {
    private volatile boolean done;
    private volatile boolean success;
    private volatile T value;
    private volatile Throwable cause;
    private final CountDownLatch latch = new CountDownLatch(1);
    private final List<BiConsumer<T, Throwable>> listeners = new ArrayList<>();

    @Override
    public synchronized Promise<T> setSuccess(T value) {
        if (done) throw new IllegalStateException("Already completed");
        this.value = value;
        this.success = true;
        complete();
        return this;
    }

    @Override
    public synchronized Promise<T> setFailure(Throwable cause) {
        if (done) throw new IllegalStateException("Already completed");
        this.cause = Objects.requireNonNull(cause, "cause");
        this.success = false;
        complete();
        return this;
    }

    @Override
    public synchronized Promise<T> trySuccess(T value) {
        if (done) return this;
        this.value = value;
        this.success = true;
        complete();
        return this;
    }

    @Override
    public synchronized Promise<T> tryFailure(Throwable cause) {
        if (done) return this;
        this.cause = Objects.requireNonNull(cause, "cause");
        this.success = false;
        complete();
        return this;
    }

    private void complete() {
        done = true;
        latch.countDown();
        for (BiConsumer<T, Throwable> listener : listeners) {
            try { listener.accept(value, cause); } catch (Throwable ignored) {}
        }
        listeners.clear();
    }

    @Override
    public boolean isSuccess() { return done && success; }

    @Override
    public boolean isDone() { return done; }

    @Override
    public Throwable cause() { return cause; }

    @Override
    public T getNow() { return value; }

    @Override
    public synchronized Future<T> addListener(BiConsumer<T, Throwable> listener) {
        if (done) {
            listener.accept(value, cause);
        } else {
            listeners.add(listener);
        }
        return this;
    }

    @Override
    public <U> Future<U> map(Function<? super T, ? extends U> mapper) {
        DefaultPromise<U> mapped = new DefaultPromise<>();
        addListener((v, c) -> {
            if (c != null) mapped.tryFailure(c);
            else {
                try { mapped.trySuccess(mapper.apply(v)); }
                catch (Throwable t) { mapped.tryFailure(t); }
            }
        });
        return mapped;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException {
        if (!done) latch.await(timeout, unit);
        return value;
    }
}

