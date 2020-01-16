package io.bitchat.im.server.processor.login;

import io.bitchat.core.ServerAttr;
import io.bitchat.im.ImServiceName;
import io.bitchat.im.PojoResult;
import io.bitchat.im.server.BeanUtil;
import io.bitchat.im.server.service.user.UserService;
import io.bitchat.im.server.session.ImSession;
import io.bitchat.im.server.session.ImSessionManager;
import io.bitchat.im.user.User;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.PayloadFactory;
import io.bitchat.packet.processor.AbstractRequestProcessor;
import io.bitchat.packet.processor.Processor;
import io.bitchat.server.ServerAttrHolder;
import io.bitchat.server.channel.ChannelHelper;
import io.bitchat.server.channel.ChannelType;
import io.bitchat.server.session.SessionHelper;
import io.bitchat.server.session.SessionManager;
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
    private SessionManager sessionManager;

    public LoginProcessor() {
        this.userService = BeanUtil.getBean(UserService.class);
        this.sessionManager = ImSessionManager.getInstance();
    }

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        // transfer map to bean
        LoginRequest loginRequest = cn.hutool.core.bean.BeanUtil.mapToBean(params, LoginRequest.class, false);
        Channel channel = ctx.channel();
        PojoResult<User> pojoResult = userService.login(loginRequest);
        Payload payload;
        if (pojoResult.isSuccess()) {
            User user = pojoResult.getContent();
            ChannelType channelType = ChannelHelper.getChannelType(channel);
            boolean alreadyLogin = sessionManager.exists(channelType, user.getUserId());
            if (alreadyLogin) {
                payload = PayloadFactory.newErrorPayload(ResultCode.RECORD_ALREADY_EXISTS.getCode(), "该用户已经在其他设备登录");
            } else {
                payload = PayloadFactory.newSuccessPayload();
                boundSession(channel, user);
            }
        } else {
            payload = PayloadFactory.newErrorPayload(pojoResult.getErrorCode(), pojoResult.getErrorMsg());
        }
        return payload;
    }

    private synchronized void boundSession(Channel channel, User user) {
        ServerAttr serverAttr = ServerAttrHolder.get();
        ImSession imSession = (ImSession) sessionManager.newSession();
        imSession.setUserName(user.getUserName());
        imSession.setServerAddress(serverAttr.getAddress());
        imSession.setServerPort(serverAttr.getPort());
        // bound the session with channelId
        sessionManager.bound(imSession, channel.id(), user.getUserId());
        SessionHelper.markOnline(channel, imSession.sessionId());
    }


}
