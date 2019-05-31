package io.bitchat.server.handler;

import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.PacketSymbol;
import io.bitchat.transport.PacketSymbols;
import io.bitchat.transport.RegisterRequestPacket;
import io.bitchat.user.UserService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@Bean
@PacketSymbol(PacketSymbols.REGISTER_REQUEST_PACKET)
public class RegisterRequestPacketHandler implements PacketHandler<RegisterRequestPacket, CarrierPacket<String>> {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CarrierPacket<String> handle(ChannelHandlerContext ctx, RegisterRequestPacket packet) {
        CarrierPacket<String> response = CarrierPacket.getStringCarrierPacket(false, "Need to be handled", null);
        response.setId(packet.getId());
        return response;
    }


}
