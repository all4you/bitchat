package io.bitchat.server;

import io.bitchat.connection.ConnectionUtil;
import io.bitchat.core.Carrier;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.protocol.packet.Packet;
import io.bitchat.protocol.packet.ReservedSymbols;
import io.bitchat.transport.PacketSymbols;
import io.netty.channel.Channel;

/**
 * @author houyi
 */
public class LoginInterceptor extends Interceptor {

    @Override
    public Carrier<Packet> preHandle(Channel channel, Packet packet) {
        boolean shouldCheckLogin = false;
        int symbol = packet.getSymbol();
        if (!(symbol == PacketSymbols.LOGIN_REQUEST_PACKET || symbol == ReservedSymbols.PING_PACKET)) {
            shouldCheckLogin = true;
        }
        if (shouldCheckLogin && !ConnectionUtil.hasLogin(channel)) {
            CarrierPacket<String> response = CarrierPacket.getStringCarrierPacket(false, "Not logged in yet!", null);
            response.setId(packet.getId());
            return Carrier.<Packet>builder().success(false).data(response).build();
        }
        if(symbol == PacketSymbols.LOGIN_REQUEST_PACKET && ConnectionUtil.hasLogin(channel)) {
            CarrierPacket<String> response = CarrierPacket.getStringCarrierPacket(false, "Already logged in!", null);
            response.setId(packet.getId());
            return Carrier.<Packet>builder().success(false).data(response).build();
        }
        return Interceptor.SUCCESS;
    }


}
