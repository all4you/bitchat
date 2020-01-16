package io.bitchat.server.session;

import io.bitchat.server.channel.ChannelType;
import io.netty.channel.ChannelId;

import java.util.List;

/**
 * @author houyi
 */
public interface SessionManager {

    boolean exists(ChannelType channelType, long userId);

    Session newSession();

    void bound(Session session, ChannelId channelId, long userId);

    void removeSession(ChannelId channelId);

    Session getSession(String sessionId);

    List<Session> getSessionsByUserId(long userId);

    List<Session> getAllSessions();

}
