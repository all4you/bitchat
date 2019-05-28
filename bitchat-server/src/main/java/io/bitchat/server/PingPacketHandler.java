package io.bitchat.server;

import io.bitchat.protocol.packet.*;
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
@PacketSymbol(ReservedSymbols.PING_PACKET)
public class PingPacketHandler implements PacketHandler<PingPacket, PongPacket> {

    @Override
    public PongPacket handle(ChannelHandlerContext ctx, PingPacket packet) {
        log.debug("Return a PongPacket");
        return new PongPacket();
    }

}
