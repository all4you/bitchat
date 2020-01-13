package io.bitchat.server.session;

import io.netty.channel.ChannelId;

import java.util.Collection;

/**
 * @author houyi
 */
public interface SessionManager {

    Session newSession();

    void bound(Session session, ChannelId channelId);

    void removeSession(ChannelId channelId);

    Session getSession(String sessionId);

    Collection<Session> getAllSessions();

}
