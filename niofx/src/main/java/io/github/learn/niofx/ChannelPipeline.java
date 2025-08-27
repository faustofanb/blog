package io.github.learn.niofx;

public interface ChannelPipeline {
	Channel channel();
	ChannelPipeline addLast(String name, ChannelHandler handler);
	ChannelPipeline addLast(ChannelHandler handler);
	ChannelPipeline remove(String name);
	ChannelPipeline remove(ChannelHandler handler);
	ChannelHandler first();
	ChannelHandler last();
}
