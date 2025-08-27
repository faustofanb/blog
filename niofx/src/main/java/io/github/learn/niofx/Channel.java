package io.github.learn.niofx;

import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.CompletableFuture;

public interface Channel {
	SelectableChannel javaChannel();
	SocketAddress localAddress();
	SocketAddress remoteAddress();
	ChannelPipeline pipeline();
	boolean isActive();
	CompletableFuture<Void> close();
}
