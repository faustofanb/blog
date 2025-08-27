package com.minetty.channel;

public interface ChannelPipeline {
    ChannelPipeline addLast(ChannelHandler handler);

    ChannelPipeline fireChannelActive();
    ChannelPipeline fireChannelInactive();
    ChannelPipeline fireChannelRead(Object msg);
    ChannelPipeline fireExceptionCaught(Throwable cause);
}

