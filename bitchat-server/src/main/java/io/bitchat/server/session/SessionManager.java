package io.bitchat.server.session;

import io.netty.channel.ChannelId;

import java.util.Collection;

/**
 * @author houyi
 */
public interface SessionManager {

    Session newSession();

    void bound(Session session, ChannelId channelId, long userId);

    void removeSession(ChannelId channelId);

    Session getSession(String sessionId);

    Collection<Session> getSessionsByUserId(long userId);

    Collection<Session> getAllSessions();

}
