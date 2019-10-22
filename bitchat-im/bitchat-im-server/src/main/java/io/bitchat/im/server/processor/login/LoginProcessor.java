package io.bitchat.im.server.processor.login;

import io.bitchat.im.ImServiceName;
import io.bitchat.im.server.connection.ConnectionManager;
import io.bitchat.im.server.connection.DefaultConnectionManager;
import io.bitchat.core.ServerAttr;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.protocol.AbstractRequestProcessor;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
import io.bitchat.protocol.Processor;
import io.bitchat.server.ServerAttrHolder;
import io.bitchat.im.server.user.DefaultUserService;
import io.bitchat.im.user.User;
import io.bitchat.im.server.user.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
@Processor(name = ImServiceName.LOGIN)
public class LoginProcessor extends AbstractRequestProcessor {

    private UserService userService;
    private ConnectionManager connectionManager;

    public LoginProcessor() {
        this.userService = DefaultUserService.getInstance();
        this.connectionManager = DefaultConnectionManager.getInstance();
    }

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        // transfer map to bean
        LoginRequest loginRequest = mapToBean(params, LoginRequest.class);
        User user = login(loginRequest.getUserName(), loginRequest.getPassword());
        boolean success = user != null;
        Payload payload = success ?
                PayloadFactory.newSuccessPayload() :
                PayloadFactory.newErrorPayload(ResultCode.BIZ_FAIL.getCode(), "Login fail, please check your account and password");
        storeConnection(ctx, user);
        return payload;
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
