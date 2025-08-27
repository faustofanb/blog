package com.minetty.channel;

import com.minetty.concurrent.DefaultPromise;
import com.minetty.concurrent.Future;

import java.util.ArrayList;
import java.util.List;

final class DefaultChannelPipeline implements ChannelPipeline {
    private final Channel channel;
    private final List<HandlerContext> contexts = new ArrayList<>();

    DefaultChannelPipeline(Channel channel) {
        this.channel = channel;
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler handler) {
        contexts.add(new HandlerContext(handler));
        return this;
    }

    @Override
    public ChannelPipeline fireChannelActive() {
        for (HandlerContext ctx : contexts) ctx.handler.channelActive(ctx);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelInactive() {
        for (HandlerContext ctx : contexts) ctx.handler.channelInactive(ctx);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelRead(Object msg) {
        for (HandlerContext ctx : contexts) ctx.handler.channelRead(ctx, msg);
        return this;
    }

    @Override
    public ChannelPipeline fireExceptionCaught(Throwable cause) {
        for (HandlerContext ctx : contexts) ctx.handler.exceptionCaught(ctx, cause);
        return this;
    }

    private final class HandlerContext implements ChannelHandlerContext {
        private final ChannelHandler handler;

        private HandlerContext(ChannelHandler handler) {
            this.handler = handler;
        }

        @Override
        public Channel channel() { return channel; }

        @Override
        public ChannelPipeline pipeline() { return DefaultChannelPipeline.this; }

        @Override
        public Future<Void> write(Object msg) {
            // No outbound pipeline yet; stub success
            return new DefaultPromise<Void>().setSuccess(null);
        }

        @Override
        public Future<Void> flush() {
            return new DefaultPromise<Void>().setSuccess(null);
        }

        @Override
        public void fireChannelActive() { DefaultChannelPipeline.this.fireChannelActive(); }

        @Override
        public void fireChannelInactive() { DefaultChannelPipeline.this.fireChannelInactive(); }

        @Override
        public void fireChannelRead(Object msg) { DefaultChannelPipeline.this.fireChannelRead(msg); }

        @Override
        public void fireExceptionCaught(Throwable cause) { DefaultChannelPipeline.this.fireExceptionCaught(cause); }
    }
}

