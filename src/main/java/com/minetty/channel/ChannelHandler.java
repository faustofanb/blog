package com.minetty.channel;

public interface ChannelHandler {
    default void channelActive(ChannelHandlerContext ctx) {}
    default void channelInactive(ChannelHandlerContext ctx) {}
    default void channelRead(ChannelHandlerContext ctx, Object msg) {}
    default void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {}
}

