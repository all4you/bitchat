package io.bitchat.server.handler;

import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.core.connection.ConnectionManager;
import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.lang.constants.ResultCode;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.user.UserService;
import io.bitchat.protocol.packet.MsgCarrierPacket;
import io.bitchat.protocol.packet.RegisterRequestPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@Bean
public class RegisterRequestPacketHandler implements PacketHandler<RegisterRequestPacket, MsgCarrierPacket<String>> {

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionManager connectionManager;

    @Override
    public int symbol() {
        return PacketSymbols.REGISTER_REQUEST_PACKET;
    }

    @Override
    public MsgCarrierPacket<String> handle(ChannelHandlerContext ctx, RegisterRequestPacket packet) {

        MsgCarrierPacket<String> response = MsgCarrierPacket.<String>builder()
                .code(ResultCode.SUCCESS)
                .success(false)
                .msg("Need to be handled")
                .build();
        response.setId(packet.getId());
        return response;
    }


}
