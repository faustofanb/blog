package com.minetty.eventloop;

import com.minetty.concurrent.Future;

public interface EventLoop {
    void execute(Runnable task);
    Future<?> submit(Runnable task);
    void shutdown();
}

