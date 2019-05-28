package io.bitchat.client.handler;

import io.bitchat.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.PacketSymbol;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.transport.P2pMsgPushPacket;
import io.bitchat.transport.PacketSymbols;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@PacketSymbol(PacketSymbols.P2P_MSG_PUSH_PACKET)
public class P2pMsgPushPacketHandler implements PacketHandler<P2pMsgPushPacket, CarrierPacket<String>> {

    @Override
    public CarrierPacket<String> handle(ChannelHandlerContext ctx, P2pMsgPushPacket packet) {
        Long partnerId = packet.getPartnerId();
        String partnerName = packet.getPartnerName();
        String msg = packet.getMsg();
        System.out.println(String.format("%s(%d):\t%s", partnerName, partnerId, msg));
        System.out.println("bitchat> ");
        return null;
    }

}
