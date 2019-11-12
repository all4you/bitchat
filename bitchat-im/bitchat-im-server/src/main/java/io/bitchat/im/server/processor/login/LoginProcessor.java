package io.bitchat.im.server.processor.login;

import io.bitchat.core.ServerAttr;
import io.bitchat.im.ImServiceName;
import io.bitchat.im.PojoResult;
import io.bitchat.im.server.BeanUtil;
import io.bitchat.im.server.connection.ConnectionManager;
import io.bitchat.im.server.connection.DefaultConnectionManager;
import io.bitchat.im.server.service.user.UserService;
import io.bitchat.im.user.User;
import io.bitchat.protocol.AbstractRequestProcessor;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
import io.bitchat.protocol.Processor;
import io.bitchat.server.ServerAttrHolder;
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
        this.userService = BeanUtil.getBean(UserService.class);
        this.connectionManager = DefaultConnectionManager.getInstance();
    }

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        // transfer map to bean
        LoginRequest loginRequest = mapToBean(params, LoginRequest.class);
        PojoResult<User> pojoResult = userService.login(loginRequest);
        Payload payload = pojoResult.isSuccess() ?
                PayloadFactory.newSuccessPayload() :
                PayloadFactory.newErrorPayload(pojoResult.getErrorCode(), pojoResult.getErrorMsg());
        storeConnection(ctx, pojoResult.getContent());
        return payload;
    }

    private synchronized void storeConnection(ChannelHandlerContext ctx, User user) {
        Channel channel = ctx.channel();
        if (user != null && !connectionManager.contains(channel)) {
            ServerAttr serverAttr = ServerAttrHolder.get();
            connectionManager.add(channel, user, serverAttr.getAddress(), serverAttr.getPort());
        }
    }


}
