package io.bitchat.server.handler;

import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.bitchat.connection.ConnectionManager;
import io.bitchat.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.PacketSymbol;
import io.bitchat.core.ServerAttr;
import io.bitchat.server.ServerAttrHolder;
import io.bitchat.protocol.packet.CarrierPacket;
import io.bitchat.transport.LoginRequestPacket;
import io.bitchat.transport.PacketSymbols;
import io.bitchat.user.User;
import io.bitchat.user.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@Bean
@PacketSymbol(PacketSymbols.LOGIN_REQUEST_PACKET)
public class LoginRequestPacketHandler implements PacketHandler<LoginRequestPacket, CarrierPacket<String>> {

    private UserService userService;

    private ConnectionManager connectionManager;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public CarrierPacket<String> handle(ChannelHandlerContext ctx, LoginRequestPacket packet) {
        User user = login(packet.getUserName(), packet.getPassword());
        boolean success = user != null;
        String msg = "Success";
        if (!success) {
            msg = "Login fail, please check your account and password";
        }
        CarrierPacket<String> response = CarrierPacket.getStringCarrierPacket(success, msg, null);
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
            ServerAttr serverAttr = ServerAttrHolder.get();
            connectionManager.add(channel, user, serverAttr.getAddress(), serverAttr.getPort());
        }
    }


}
