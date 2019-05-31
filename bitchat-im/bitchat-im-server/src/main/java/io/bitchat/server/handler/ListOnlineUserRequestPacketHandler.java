package io.bitchat.server.handler;

import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.connection.ConnectionManager;
import io.bitchat.core.lang.constants.ResultCode;
import io.bitchat.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.PacketSymbol;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.transport.ListOnlineUserRequestPacket;
import io.bitchat.transport.PacketSymbols;
import io.bitchat.user.User;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author houyi
 */
@Slf4j
@Bean
@PacketSymbol(PacketSymbols.LIST_ONLINE_USER_REQUEST_PACKET)
public class ListOnlineUserRequestPacketHandler implements PacketHandler<ListOnlineUserRequestPacket, CarrierPacket<List<User>>> {

    private ConnectionManager connectionManager;

    @Autowired
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public CarrierPacket<List<User>> handle(ChannelHandlerContext ctx, ListOnlineUserRequestPacket packet) {
        CarrierPacket<List<User>> response = CarrierPacket.<List<User>>builder()
                .code(ResultCode.SUCCESS)
                .success(true)
                .msg("SUCCESS")
                .data(connectionManager.onlineUser())
                .build();
        response.setId(packet.getId());
        return response;
    }


}
