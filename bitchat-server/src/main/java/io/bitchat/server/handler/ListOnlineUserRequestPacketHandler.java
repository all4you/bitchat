package io.bitchat.server.handler;

import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.core.connection.ConnectionManager;
import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.lang.constants.ResultCode;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.user.User;
import io.bitchat.protocol.packet.ListOnlineUserRequestPacket;
import io.bitchat.protocol.packet.CarrierPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author houyi
 */
@Slf4j
@Bean
public class ListOnlineUserRequestPacketHandler implements PacketHandler<ListOnlineUserRequestPacket, CarrierPacket<List<User>>> {

    @Autowired
    private ConnectionManager connectionManager;

    @Override
    public int symbol() {
        return PacketSymbols.LIST_ONLINE_USER_REQUEST_PACKET;
    }

    @Override
    public CarrierPacket<List<User>> handle(ChannelHandlerContext ctx, ListOnlineUserRequestPacket packet) {
        CarrierPacket<List<User>> response = CarrierPacket.<List<User>>builder()
                .code(ResultCode.SUCCESS)
                .success(true)
                .msg("success")
                .data(connectionManager.onlineUser())
                .build();
        response.setId(packet.getId());
        return response;
    }


}
