package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.bean.DefaultBeanContext;
import io.bitchat.connection.ConnectionManager;
import io.bitchat.connection.ConnectionUtil;
import io.bitchat.core.executor.Executor;
import io.bitchat.core.executor.PacketExecutor;
import io.bitchat.core.lang.constants.AsyncHandle;
import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.protocol.PacketRecognizer;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.protocol.packet.CarrierPacket;
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

    private ConnectionManager connectionManager;

    private ServerPacketDispatcher(PacketRecognizer recognizer) {
        Assert.notNull(recognizer, "recognizer can not be null");
        this.executor = PacketExecutor.getInstance(recognizer);
        this.connectionManager = DefaultBeanContext.getInstance().getBean("memoryConnectionKeeper", ConnectionManager.class);
    }

    public static ServerPacketDispatcher getInstance(PacketRecognizer recognize) {
        return Singleton.get(ServerPacketDispatcher.class, recognize);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet request) {
        // TODO how ServerSpeaker communicate with each other
        // if the packet is not a [login|ping] request
        // then we should check the login state of the channel
        boolean shouldCheckLogin = false;
        int symbol = request.getSymbol();
        if (!(symbol == PacketSymbols.LOGIN_REQUEST_PACKET || symbol == PacketSymbols.PING_PACKET)) {
            shouldCheckLogin = true;
        }
        if (shouldCheckLogin && !ConnectionUtil.hasLogin(ctx.channel())) {
            CarrierPacket<String> response = CarrierPacket.getStringCarrierPacket(false, "Not logged in yet!", null);
            response.setId(request.getId());
            writeResponse(ctx, response);
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
        connectionManager.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error("ctx close,cause:", cause);
    }

    private void writeResponse(ChannelHandlerContext ctx, Packet response) {
        if (response != null) {
            ctx.channel().writeAndFlush(response);
        }
    }

}
