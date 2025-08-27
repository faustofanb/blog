package io.github.learn.niofx;

import java.util.concurrent.CompletableFuture;

public interface ChannelHandlerContext {
	Channel channel();
	ChannelPipeline pipeline();
	CompletableFuture<Void> write(Object msg);
	CompletableFuture<Void> flush();
	void fireChannelRegistered();
	void fireChannelActive();
	void fireChannelRead(Object msg);
	void fireChannelInactive();
	void fireExceptionCaught(Throwable cause);
}
