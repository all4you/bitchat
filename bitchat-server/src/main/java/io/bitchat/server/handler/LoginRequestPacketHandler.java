package io.bitchat.server.handler;

import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.core.connection.ConnectionManager;
import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.lang.constants.ResultCode;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.user.User;
import io.bitchat.core.user.UserService;
import io.bitchat.protocol.packet.LoginRequestPacket;
import io.bitchat.protocol.packet.MsgCarrierPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@Bean
public class LoginRequestPacketHandler implements PacketHandler<LoginRequestPacket, MsgCarrierPacket<String>> {

    @Autowired
    private UserService userService;

    @Autowired
    private ConnectionManager connectionManager;

    @Override
    public int symbol() {
        return PacketSymbols.LOGIN_REQUEST_PACKET;
    }

    @Override
    public MsgCarrierPacket<String> handle(ChannelHandlerContext ctx, LoginRequestPacket packet) {
        User user = login(packet.getUserName(), packet.getPassword());
        boolean success = user != null;
        String msg = "Success";
        if(!success) {
            msg = "Login fail, please check your account and password";
        }
        MsgCarrierPacket<String> response = MsgCarrierPacket.<String>builder()
                .code(ResultCode.SUCCESS)
                .success(success)
                .msg(msg)
                .build();
        response.setId(packet.getId());
        storeConnection(ctx, user);
        return response;
    }

    private User login(String userName, String password) {
        User user = null;
        try {
            user = userService.login(userName, password);
        } catch (Exception e) {
            log.warn("userService login error, cause:{}", e.getMessage(), e);
        }
        return user;
    }

    private synchronized void storeConnection(ChannelHandlerContext ctx, User user) {
        Channel channel = ctx.channel();
        if (user != null && !connectionManager.contains(channel)) {
            connectionManager.put(user, channel);
        }
    }


}
