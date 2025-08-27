package com.minetty.eventloop;

import com.minetty.concurrent.DefaultPromise;
import com.minetty.concurrent.Future;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Skeleton NIO event loop with a task queue. Selector usage will follow.
 */
public final class NioEventLoop implements EventLoop, Runnable {
    private final Selector selector;
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Thread thread;

    public NioEventLoop() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.thread = new Thread(this, "nio-event-loop");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    @Override
    public void run() {
        while (running.get()) {
            // Process tasks
            Runnable task;
            while ((task = tasks.poll()) != null) {
                try { task.run(); } catch (Throwable ignored) {}
            }

            try {
                selector.select(50);
            } catch (IOException e) {
                // log and continue in educational code
            }
            // TODO: handle selected keys in future steps
        }
    }

    @Override
    public void execute(Runnable task) {
        tasks.offer(task);
        selector.wakeup();
    }

    @Override
    public Future<?> submit(Runnable task) {
        DefaultPromise<Object> p = new DefaultPromise<>();
        execute(() -> {
            try { task.run(); p.trySuccess(null); }
            catch (Throwable t) { p.tryFailure(t); }
        });
        return p;
    }

    @Override
    public void shutdown() {
        running.set(false);
        selector.wakeup();
    }

    public Selector selector() { return selector; }
}

