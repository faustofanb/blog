package io.github.learn.niofx;

@FunctionalInterface
public interface ChannelInitializer<C extends Channel> {
	void initChannel(C ch) throws Exception;
}
