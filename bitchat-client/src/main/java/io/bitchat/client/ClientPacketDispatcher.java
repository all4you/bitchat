package io.bitchat.client;

import cn.hutool.core.lang.Assert;
import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.packet.Packet;
import io.bitchat.protocol.packet.PacketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author houyi
 */
@Slf4j
public class ClientPacketDispatcher extends SimpleChannelInboundHandler<Packet> {

    private PacketRecognizer recognizer;

    public ClientPacketDispatcher(PacketRecognizer recognizer) {
        Assert.notNull(recognizer, "recognizer can not be null");
        this.recognizer = recognizer;
    }

    /**
     * there are two ways the client will receive the response
     * 1: the result packet which the client request
     * this kind of response will be handled by client itself
     *
     * 2: the message packet which the server pushed initiative
     * this kind of response will be handled by {@link PacketHandler}
     *
     * @param ctx      the context
     * @param response the response
     */
    @SuppressWarnings("unchecked")
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet response) {
        log.debug("ClientPacketDispatcher has received {}", response);
        int symbol = response.getSymbol();
        PacketHandler handler = recognizer.packetHandler(symbol);
        if (handler != null) {
            handler.handle(ctx, response);
        } else {
            CompletableFuture<Packet> pending = PendingRequests.remove(response.getId());
            if (pending != null) {
                // the response will be handled by client
                // after the client future has been notified
                // to be completed
                pending.complete(response);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error("ctx close,cause:", cause);
    }

}
