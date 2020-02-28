package io.bitchat.server.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.packet.Command;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.CommandFactory;
import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.packet.factory.PayloadFactory;
import io.bitchat.server.channel.ChannelType;
import io.bitchat.ws.FrameFactory;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.List;

/**
 * @author houyi
 */
public class SessionHelper {

    private static final AttributeKey<String> SESSION_ID = AttributeKey.newInstance("sessionId");

    private static SessionManager sessionManager = DefaultSessionManager.getInstance();

    private SessionHelper() {

    }

    /**
     * mark this channel with attribute sessionId
     *
     * @param channel the channel
     */
    public static void markOnline(Channel channel, String sessionId) {
        channel.attr(SESSION_ID).set(sessionId);
    }


    /**
     * mark this channel with attribute sessionId
     *
     * @param channel the channel
     */
    public static void markOffline(Channel channel) {
        channel.attr(SESSION_ID).set(null);
    }

    /**
     * check whether the channel is login
     *
     * @param channel the channel
     * @return true if logged in otherwise false
     */
    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(SESSION_ID) && channel.attr(SESSION_ID).get() != null;
    }

    public static String getSessionId(Channel channel) {
        return channel.hasAttr(SESSION_ID) ? channel.attr(SESSION_ID).get() : null;
    }

    public static Session getSession(Channel channel) {
        return getSession(getSessionId(channel));
    }

    public static Session getSession(String sessionId) {
        return sessionManager.getSession(sessionId);
    }

    public static List<Session> getSessionsByUserId(long userId) {
        return sessionManager.getSessionsByUserId(userId);
    }

    public static List<Session> getSessionsByUserIdAndChannelType(long userId, ChannelType channelType) {
        return sessionManager.getSessionsByUserIdAndChannelType(userId, channelType);
    }

    public static List<Session> getAllSessions() {
        return sessionManager.getAllSessions();
    }

    public static Payload push(String sessionId, String commandName, String contentJson) {
        Session session = getSession(sessionId);
        if (session == null) {
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
            Command command = CommandFactory.newCommand(commandName, jsonObject);
            // 推送 Packet 格式的数据
            pushObj = PacketFactory.newCmdPacket(command);
        } else {
            // 推送 Frame 格式的数据
            pushObj = FrameFactory.newCmdFrame(commandName, jsonObject);
        }
        session.writeAndFlush(pushObj);
        return PayloadFactory.newSuccessPayload();
    }

}
