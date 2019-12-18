package io.bitchat.client;

import io.bitchat.packet.PendingPackets;
import io.bitchat.packet.*;
import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.packet.ctx.CommandProcessorContext;
import io.bitchat.packet.ctx.RequestProcessorContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author houyi
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    private RequestProcessorContext requestHandler;
    private CommandProcessorContext commandHandler;

    public ClientHandler() {
        this.requestHandler = RequestProcessorContext.getInstance();
        this.commandHandler = CommandProcessorContext.getInstance();
    }

    /**
     * there are three kind of packet the client will receive
     * 1: a request packet
     * this kind of packet will be handled by client itself
     * see {@link RequestProcessorContext}
     * <p>
     * 2: a response packet
     * this kind of packet will be handled or not due to biz
     * <p>
     * 3: a command packet
     * this kind of packet is a one way packet sent by server
     *
     * @param ctx    the context
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
        Payload payload = requestHandler.process(ctx, packet.getRequest());
        Packet response = PacketFactory.newResponsePacket(payload, packet.getId());
        writeResponse(ctx, response);
    }

    private void writeResponse(ChannelHandlerContext ctx, Packet response) {
        if (response != null) {
            ctx.channel().writeAndFlush(response);
        }
    }

    private void onResponse(Packet packet) {
        CompletableFuture<Packet> pending = PendingPackets.remove(packet.getId());
        if (pending != null) {
            pending.complete(packet);
        }
    }

    public void onCommand(ChannelHandlerContext ctx, Packet packet) {
        commandHandler.process(ctx, packet.getCommand());
    }

}
