package io.bitchat.server.handler;

import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.protocol.packet.PacketSymbol;
import io.bitchat.protocol.packet.PingPacket;
import io.bitchat.protocol.packet.PongPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * A {@link PingPacket} handler
 * just return a {@link PongPacket}
 * </p>
 *
 * @author houyi
 */
@Slf4j
@PacketSymbol(PacketSymbols.PING_PACKET)
public class PingPacketHandler implements PacketHandler<PingPacket, PongPacket> {

    @Override
    public PongPacket handle(ChannelHandlerContext ctx, PingPacket packet) {
        log.debug("Return a PongPacket");
        return new PongPacket();
    }

}
