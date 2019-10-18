package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.PendingRequests;
import io.bitchat.core.executor.Executor;
import io.bitchat.interceptor.InterceptorHandler;
import io.bitchat.protocol.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * Server packet ChannelHandler
 * </p>
 *
 * @author houyi
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    private Executor<Packet> executor;

    private ChannelListener channelListener;

    private ServerHandler() {

    }

    private ServerHandler(ChannelListener channelListener) {
        Assert.notNull(channelListener, "channelListener can not be null");
        this.executor = PacketExecutor.getInstance();
        this.channelListener = channelListener;
    }

    public static ServerHandler getInstance(ChannelListener channelListener) {
        return Singleton.get(ServerHandler.class, channelListener);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Received an active channel:{}", ctx.channel());
        channelListener.channelActive(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        byte type = packet.getType();
        if (type == PacketType.PACKET_TYPE_REQUEST) {
            handleRequest(ctx, packet);
        } else {
            onResponse(packet);
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

    private void handleRequest(ChannelHandlerContext ctx, Packet packet) {
        // TODO how ServerSpeaker communicate with each other
        // pre handle
        Payload payload = InterceptorHandler.preHandle(ctx.channel(), packet);
        if (!payload.isSuccess()) {
            Packet response = PacketFactory.newResponsePacket(payload, packet.getId());
            writeResponse(ctx, response);
            return;
        }
        // if the packet should be handled async
        if (packet.isHandleAsync()) {
            EventExecutor channelExecutor = ctx.executor();
            // create a promise
            Promise<Packet> promise = new DefaultPromise<>(channelExecutor);
            // async execute and get a future
            Future<Packet> future = executor.asyncExecute(promise, ctx, packet);
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
            Packet response = executor.execute(ctx, packet);
            writeResponse(ctx, response);
        }
    }

    private void writeResponse(ChannelHandlerContext ctx, Packet response) {
        if (response != null) {
            ctx.channel().writeAndFlush(response);
        }
    }

    private void onResponse(Packet packet) {
        CompletableFuture<Packet> pending = PendingRequests.remove(packet.getId());
        if (pending != null) {
            // the response will be handled by client
            // after the client future has been notified
            // to be completed
            pending.complete(packet);
        }
    }


}
