package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.Carrier;
import io.bitchat.core.executor.Executor;
import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.packet.AsyncHandle;
import io.bitchat.protocol.packet.Packet;
import io.bitchat.protocol.packet.PacketExecutor;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Server packet ChannelHandler
 * </p>
 *
 * @author houyi
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerPacketDispatcher extends SimpleChannelInboundHandler<Packet> {

    private Executor<Packet> executor;

    private ChannelListener channelListener;

    private ServerPacketDispatcher() {

    }

    private ServerPacketDispatcher(PacketRecognizer recognizer, ChannelListener channelListener) {
        Assert.notNull(recognizer, "recognizer can not be null");
        Assert.notNull(channelListener, "channelListener can not be null");
        this.executor = PacketExecutor.getInstance(recognizer);
        this.channelListener = channelListener;
    }

    public static ServerPacketDispatcher getInstance(PacketRecognizer recognize, ChannelListener channelListener) {
        return Singleton.get(ServerPacketDispatcher.class, recognize, channelListener);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Received an active channel:{}", ctx.channel());
        channelListener.channelActive(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet request) {
        // TODO how ServerSpeaker communicate with each other
        // pre handle
        Carrier<Packet> carrier = InterceptorHandler.preHandle(ctx.channel(), request);
        if (!carrier.isSuccess()) {
            writeResponse(ctx, carrier.getData());
            return;
        }

        // if the packet should be handled async
        if (request.getAsync() == AsyncHandle.ASYNC) {
            EventExecutor channelExecutor = ctx.executor();
            // create a promise
            Promise<Packet> promise = new DefaultPromise<>(channelExecutor);
            // async execute and get a future
            Future<Packet> future = executor.asyncExecute(promise, ctx, request);
            future.addListener(new GenericFutureListener<Future<Packet>>() {
                @Override
                public void operationComplete(Future<Packet> f) throws Exception {
                    if (f.isSuccess()) {
                        Packet response = f.get();
                        writeResponse(ctx, response);
                    }
                }
            });
        } else {
            // sync execute and get the response packet
            Packet response = executor.execute(ctx, request);
            writeResponse(ctx, response);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("The channel has been inactive will remove it:{}", ctx.channel());
        channelListener.channelInactive(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception occurred cause={} will close the channel:{}", cause.getMessage(), ctx.channel(), cause);
        ctx.close();
    }

    private void writeResponse(ChannelHandlerContext ctx, Packet response) {
        if (response != null) {
            ctx.channel().writeAndFlush(response);
        }
    }

}
