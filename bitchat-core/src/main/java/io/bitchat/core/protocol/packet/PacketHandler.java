package io.bitchat.core.protocol.packet;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author houyi
 */
public interface PacketHandler<REQ extends Packet, RES extends Packet> {

    /**
     * <p>
     * specify the packet symbol
     * </p>
     *
     * <p>
     * use packet symbol to mapping
     * the PacketHandler to
     * the detailed Packet
     * </p>
     *
     * @return the packet symbol
     */
    int symbol();

    /**
     * handle the packet
     *
     * @param ctx the channelHandlerContext
     * @param packet the request packet need to be handled
     * @return the response packet
     */
    RES handle(ChannelHandlerContext ctx, REQ packet);

}
