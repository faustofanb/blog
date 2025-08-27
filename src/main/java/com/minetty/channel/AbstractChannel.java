package com.minetty.channel;

import com.minetty.concurrent.DefaultPromise;
import com.minetty.concurrent.Future;

import java.net.SocketAddress;

public abstract class AbstractChannel implements Channel {
    protected final ChannelPipeline pipeline = new DefaultChannelPipeline(this);

    @Override
    public ChannelPipeline pipeline() { return pipeline; }

    @Override
    public Future<Void> write(Object msg) { return doWrite(msg); }

    @Override
    public Future<Void> flush() { return doFlush(); }

    @Override
    public Future<Void> writeAndFlush(Object msg) {
        DefaultPromise<Void> p = new DefaultPromise<>();
        doWrite(msg).addListener((v, c) -> {
            if (c != null) p.tryFailure(c); else doFlush().addListener((v2, c2) -> { if (c2 != null) p.tryFailure(c2); else p.trySuccess(null); });
        });
        return p;
    }

    protected abstract Future<Void> doWrite(Object msg);
    protected abstract Future<Void> doFlush();

    @Override
    public abstract SocketAddress localAddress();

    @Override
    public abstract SocketAddress remoteAddress();
}

