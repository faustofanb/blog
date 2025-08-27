package io.github.learn.niofx;

public interface ChannelInboundHandler extends ChannelHandler {
	default void channelRegistered(ChannelHandlerContext ctx) {}
	default void channelActive(ChannelHandlerContext ctx) {}
	default void channelRead(ChannelHandlerContext ctx, Object msg) {}
	default void channelInactive(ChannelHandlerContext ctx) {}
	default void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { cause.printStackTrace(); }
}
