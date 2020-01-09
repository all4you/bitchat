package io.bitchat.server.session;

import io.bitchat.server.channel.ChannelType;
import io.netty.channel.ChannelId;

import java.util.Collection;

/**
 * @author houyi
 */
public interface SessionManager {

    Session newSession();

    void bound(Session session, ChannelId channelId, ChannelType channelType);

    void removeSession(ChannelId channelId);

    Session getSession(String sessionId);

    Collection<Session> getAllSessions();

}
