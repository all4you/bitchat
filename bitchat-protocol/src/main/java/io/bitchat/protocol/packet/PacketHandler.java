package io.bitchat.protocol.packet;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author houyi
 */
public interface PacketHandler<REQ extends Packet, RES extends Packet> {

    /**
     * handle the packet
     *
     * @param ctx    the channelHandlerContext
     * @param packet the request packet need to be handled
     * @return the response packet
     */
    RES handle(ChannelHandlerContext ctx, REQ packet);

}
