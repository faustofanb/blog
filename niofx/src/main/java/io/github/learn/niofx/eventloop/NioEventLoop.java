package io.github.learn.niofx.eventloop;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class NioEventLoop implements EventLoop, Runnable {
	private final Selector selector;
	private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
	private final AtomicBoolean wakenUp = new AtomicBoolean(false);
	private final Thread eventLoopThread;
	private volatile boolean running = true;

	public NioEventLoop() {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.eventLoopThread = new Thread(this, "niofx-nioEventLoop");
		this.eventLoopThread.setDaemon(true);
		this.eventLoopThread.start();
	}

	@Override
	public void run() {
		while (running) {
			try {
				wakenUp.set(false);
				int selected = selector.select(1000);
				if (selected > 0) {
					processSelectedKeys(selector.selectedKeys());
				}
				runAllTasks();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try { selector.close(); } catch (IOException ignored) {}
	}

	private void processSelectedKeys(Set<SelectionKey> selectedKeys) {
		Iterator<SelectionKey> it = selectedKeys.iterator();
		while (it.hasNext()) {
			SelectionKey key = it.next();
			it.remove();
			Object att = key.attachment();
			if (att instanceof Consumer) {
				@SuppressWarnings("unchecked") Consumer<SelectionKey> consumer = (Consumer<SelectionKey>) att;
				try { consumer.accept(key); } catch (Throwable t) { t.printStackTrace(); }
			}
		}
	}

	private void runAllTasks() {
		for (Runnable r; (r = taskQueue.poll()) != null; ) {
			try { r.run(); } catch (Throwable t) { t.printStackTrace(); }
		}
	}

	@Override
	public void execute(Runnable command) {
		Objects.requireNonNull(command, "command");
		taskQueue.offer(command);
		if (!inEventLoop() && wakenUp.compareAndSet(false, true)) {
			selector.wakeup();
		}
	}

	@Override
	public void register(SelectableChannel channel, int ops, Object attachment) throws Exception {
		channel.configureBlocking(false);
		execute(() -> {
			try {
				channel.register(selector, ops, attachment);
			} catch (ClosedChannelException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void wakeup() {
		if (wakenUp.compareAndSet(false, true)) selector.wakeup();
	}

	@Override
	public boolean inEventLoop() {
		return Thread.currentThread() == eventLoopThread;
	}

	@Override
	public void shutdownGracefully() {
		running = false;
		wakeup();
	}
}
