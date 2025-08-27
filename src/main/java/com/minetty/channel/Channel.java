package com.minetty.channel;

import com.minetty.concurrent.Future;
import com.minetty.concurrent.Promise;

import java.net.SocketAddress;

public interface Channel {
    SocketAddress localAddress();
    SocketAddress remoteAddress();

    ChannelPipeline pipeline();

    boolean isOpen();
    boolean isActive();

    Future<Void> write(Object msg);
    Future<Void> flush();
    Future<Void> writeAndFlush(Object msg);

    Future<Void> close();
}

