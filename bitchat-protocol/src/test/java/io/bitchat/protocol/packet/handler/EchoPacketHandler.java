package io.bitchat.protocol.packet.handler;

import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.EchoPacket;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author houyi
 */
public class EchoPacketHandler implements PacketHandler<EchoPacket, EchoPacket> {

    @Override
    public int symbol() {
        return -1;
    }

    @Override
    public EchoPacket handle(ChannelHandlerContext ctx, EchoPacket packet) {
        return packet;
    }

}
