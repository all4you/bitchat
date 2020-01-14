package io.bitchat.server.session;

import io.netty.channel.ChannelId;

import java.util.List;

/**
 * @author houyi
 */
public interface SessionManager {

    Session newSession();

    void bound(Session session, ChannelId channelId, long userId);

    void removeSession(ChannelId channelId);

    Session getSession(String sessionId);

    List<Session> getSessionsByUserId(long userId);

    List<Session> getAllSessions();

}
