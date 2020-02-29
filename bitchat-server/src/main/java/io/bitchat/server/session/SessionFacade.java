package io.bitchat.server.session;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.packet.factory.PayloadFactory;
import io.bitchat.server.AttributeKeys;
import io.bitchat.server.channel.ChannelType;
import io.bitchat.ws.FrameFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.List;

/**
 * 封装Session的相关操作
 *
 * @author houyi
 */
public class SessionFacade {

    private SessionManager sessionManager;

    public static SessionFacade DEFAULT_FACADE = new SessionFacade(DefaultSessionManager.getInstance());

    public SessionFacade(SessionManager sessionManager) {
        Assert.notNull(sessionManager, "sessionManager can not be null");
        this.sessionManager = sessionManager;
    }

    public boolean exists(ChannelType channelType, long userId) {
        return sessionManager.exists(channelType, userId);
    }

    public boolean exists(Session session) {
        return sessionManager.exists(session);
    }

    public Session newSession() {
        return sessionManager.newSession();
    }

    public void bound(Session session, ChannelId channelId, long userId) {
        sessionManager.bound(session, channelId, userId);
    }

    public String getSessionId(Channel channel) {
        return channel.hasAttr(AttributeKeys.SESSION_ID) ? channel.attr(AttributeKeys.SESSION_ID).get() : null;
    }

    public Session getSession(Channel channel) {
        return getSession(getSessionId(channel));
    }

    public Session getSession(String sessionId) {
        return sessionManager.getSession(sessionId);
    }

    public List<Session> getSessionsByUserId(long userId) {
        return sessionManager.getSessionsByUserId(userId);
    }

    public List<Session> getSessionsByUserIdAndChannelType(long userId, ChannelType channelType) {
        return sessionManager.getSessionsByUserIdAndChannelType(userId, channelType);
    }

    public List<Session> getAllSessions() {
        return sessionManager.getAllSessions();
    }

    public Payload push(String sessionId, String commandName, String contentJson) {
        Session session = getSession(sessionId);
        return push(session, commandName, contentJson);
    }

    public Payload push(Session session, String commandName, String contentJson) {
        if (session == null || !exists(session)) {
            return PayloadFactory.newErrorPayload(ResultCode.RESOURCE_NOT_FOUND.getCode(), "Session不存在");
        }
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(contentJson);
        } catch (Exception e) {
            return PayloadFactory.newErrorPayload(ResultCode.PARAM_INVALID.getCode(), "content只支持JSON格式");
        }
        Object pushObj;
        if (session.channelType() == ChannelType.Packet) {
            // 推送 Packet 格式的数据
            pushObj = PacketFactory.newCmdPacket(commandName, jsonObject);
        } else {
            // 推送 Frame 格式的数据
            pushObj = FrameFactory.newCmdFrame(commandName, jsonObject);
        }
        session.writeAndFlush(pushObj);
        return PayloadFactory.newSuccessPayload();
    }

}
