package io.github.learn.niofx.eventloop;

public interface EventLoopGroup extends AutoCloseable {
	EventLoop next();
	int size();
	default void close() { shutdownGracefully(); }
	void shutdownGracefully();
}
