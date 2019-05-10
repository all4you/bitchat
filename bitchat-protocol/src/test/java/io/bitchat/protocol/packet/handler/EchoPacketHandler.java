package io.bitchat.protocol.packet.handler;

import io.bitchat.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.PacketSymbol;
import io.bitchat.protocol.packet.EchoPacket;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author houyi
 */
@PacketSymbol(-1)
public class EchoPacketHandler implements PacketHandler<EchoPacket, EchoPacket> {

    @Override
    public EchoPacket handle(ChannelHandlerContext ctx, EchoPacket packet) {
        return packet;
    }

}
