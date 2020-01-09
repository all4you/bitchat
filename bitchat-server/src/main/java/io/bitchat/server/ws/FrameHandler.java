package io.bitchat.server.ws;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.ws.PendingFrames;
import io.bitchat.core.executor.Executor;
import io.bitchat.packet.PacketType;
import io.bitchat.server.channel.ChannelListener;
import io.bitchat.ws.Frame;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * Server frame ChannelHandler
 * </p>
 *
 * @author houyi
 */
@Slf4j
@ChannelHandler.Sharable
public class FrameHandler extends SimpleChannelInboundHandler<Frame> {

    private Executor<Frame> executor;

    private ChannelListener channelListener;

    private FrameHandler() {

    }

    private FrameHandler(ChannelListener channelListener) {
        Assert.notNull(channelListener, "channelListener can not be null");
        this.executor = FrameExecutor.getInstance();
        this.channelListener = channelListener;
    }

    public static FrameHandler getInstance(ChannelListener channelListener) {
        return Singleton.get(FrameHandler.class, channelListener);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("FrameHandler received an active channel:{}", ctx.channel());
        channelListener.channelActive(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Frame frame) {
        byte type = frame.getType();
        if (type == PacketType.PACKET_TYPE_REQUEST) {
            onRequest(ctx, frame);
        } else {
            onResponse(frame);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("FrameHandler received an inactive channel will remove it:{}", ctx.channel());
        channelListener.channelInactive(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception occurred cause={} will close the channel:{}", cause.getMessage(), ctx.channel(), cause);
        ctx.close();
    }

    private void onRequest(ChannelHandlerContext ctx, Frame frame) {
        Frame response = executor.execute(ctx, frame);
        writeResponse(ctx, response);
    }

    private void writeResponse(ChannelHandlerContext ctx, Frame response) {
        if (response != null) {
            ctx.channel().writeAndFlush(response);
        }
    }

    private void onResponse(Frame frame) {
        CompletableFuture<Frame> pending = PendingFrames.remove(frame.getId());
        if (pending != null) {
            // the response will be handled by client
            // after the client future has been notified
            // to be completed
            pending.complete(frame);
        }
    }


}
