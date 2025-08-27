package io.github.learn.niofx.eventloop;

import java.util.concurrent.atomic.AtomicInteger;

public final class NioEventLoopGroup implements EventLoopGroup {
	private final NioEventLoop[] children;
	private final AtomicInteger idx = new AtomicInteger();

	public NioEventLoopGroup(int nThreads) {
		if (nThreads <= 0) throw new IllegalArgumentException("nThreads");
		this.children = new NioEventLoop[nThreads];
		for (int i = 0; i < nThreads; i++) {
			children[i] = new NioEventLoop();
		}
	}

	@Override
	public EventLoop next() {
		int i = Math.abs(idx.getAndIncrement() % children.length);
		return children[i];
	}

	@Override
	public int size() { return children.length; }

	@Override
	public void shutdownGracefully() {
		for (NioEventLoop child : children) child.shutdownGracefully();
	}
}
