package com.minetty.channel;

import com.minetty.concurrent.Future;

public interface ChannelHandlerContext {
    Channel channel();
    ChannelPipeline pipeline();

    Future<Void> write(Object msg);
    Future<Void> flush();
    default Future<Void> writeAndFlush(Object msg) { return write(msg).map(v -> null); }

    void fireChannelActive();
    void fireChannelInactive();
    void fireChannelRead(Object msg);
    void fireExceptionCaught(Throwable cause);
}

