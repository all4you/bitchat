package io.bitchat.client;

import io.bitchat.core.PendingRequests;
import io.bitchat.protocol.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author houyi
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    private RequestHandler requestHandler;

    public ClientHandler() {
        this.requestHandler = RequestHandler.getInstance();
    }

    /**
     * there are three kind of packet the client will receive
     * 1: a request packet
     * this kind of packet will be handled by client itself
     * see {@link RequestHandler}
     *
     * 2: a response packet
     * this kind of packet will be handled or not due to biz
     *
     * 3: a command packet
     * this kind of packet is a one way packet sent by server
     *
     * @param ctx      the context
     * @param packet the response
     */
    @SuppressWarnings("unchecked")
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        log.debug("ClientPacketDispatcher has received {}", packet);
        byte type = packet.getType();
        if (type == PacketType.PACKET_TYPE_REQUEST) {
            onRequest(ctx, packet);
        } else if (type == PacketType.PACKET_TYPE_RESPONSE) {
            onResponse(packet);
        } else {
            onCommand(ctx, packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error("ctx close,cause:", cause);
    }

    private void onRequest(ChannelHandlerContext ctx, Packet packet) {
        Payload payload = requestHandler.handle(ctx, packet.getRequest());
        Packet response = PacketFactory.newResponsePacket(payload, packet.getId());
        writeResponse(ctx, response);
    }

    private void writeResponse(ChannelHandlerContext ctx, Packet response) {
        if (response != null) {
            ctx.channel().writeAndFlush(response);
        }
    }

    private void onResponse(Packet packet) {
        CompletableFuture<Packet> pending = PendingRequests.remove(packet.getId());
        if (pending != null) {
            pending.complete(packet);
        }
    }

    public void onCommand(ChannelHandlerContext ctx, Packet packet) {
        requestHandler.handle(ctx, packet.getRequest());
    }

}
