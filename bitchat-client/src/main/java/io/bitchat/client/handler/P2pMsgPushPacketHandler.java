package io.bitchat.client.handler;

import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.MsgCarrierPacket;
import io.bitchat.protocol.packet.P2pMsgPushPacket;
import io.bitchat.protocol.packet.P2pMsgRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class P2pMsgPushPacketHandler implements PacketHandler<P2pMsgPushPacket, MsgCarrierPacket<String>> {

    @Override
    public int symbol() {
        return PacketSymbols.P2P_MSG_PUSH_PACKET;
    }

    @Override
    public MsgCarrierPacket<String> handle(ChannelHandlerContext ctx, P2pMsgPushPacket packet) {
        Long partnerId = packet.getPartnerId();
        String partnerName = packet.getPartnerName();
        String msg = packet.getMsg();
        System.out.println(String.format("%s(%d):\t%s",partnerName,partnerId,msg));
        System.out.println("bitchat> ");
        return null;
    }

}
