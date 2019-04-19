package io.bitchat.server.handler;

import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.core.connection.ConnectionManager;
import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.protocol.packet.PacketSymbol;
import io.bitchat.core.user.UserService;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.protocol.packet.RegisterRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@Bean
@PacketSymbol(PacketSymbols.REGISTER_REQUEST_PACKET)
public class RegisterRequestPacketHandler implements PacketHandler<RegisterRequestPacket, CarrierPacket<String>> {

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionManager connectionManager;

    @Override
    public CarrierPacket<String> handle(ChannelHandlerContext ctx, RegisterRequestPacket packet) {
        CarrierPacket<String> response = CarrierPacket.getStringCarrierPacket(false, "Need to be handled", null);
        response.setId(packet.getId());
        return response;
    }


}
