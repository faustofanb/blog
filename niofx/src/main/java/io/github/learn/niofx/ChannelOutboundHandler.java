package io.github.learn.niofx;

import java.util.concurrent.CompletableFuture;

public interface ChannelOutboundHandler extends ChannelHandler {
	default CompletableFuture<Void> write(ChannelHandlerContext ctx, Object msg) { return ctx.write(msg); }
	default CompletableFuture<Void> flush(ChannelHandlerContext ctx) { return ctx.flush(); }
}
