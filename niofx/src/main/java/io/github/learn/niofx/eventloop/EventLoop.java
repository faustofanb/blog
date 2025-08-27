package io.github.learn.niofx.eventloop;

import java.nio.channels.SelectableChannel;
import java.util.concurrent.Executor;

public interface EventLoop extends Executor {
	void register(SelectableChannel channel, int ops, Object attachment) throws Exception;
	void wakeup();
	boolean inEventLoop();
	void shutdownGracefully();
}
