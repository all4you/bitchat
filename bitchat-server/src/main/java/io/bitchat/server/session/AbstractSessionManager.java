package io.bitchat.server.session;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import io.bitchat.server.channel.ChannelManager;
import io.bitchat.server.channel.ChannelWrapper;
import io.bitchat.server.channel.DefaultChannelManager;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author houyi
 */
@Slf4j
public abstract class AbstractSessionManager implements SessionManager {

    private Map<String, Session> sessionMap;

    private ChannelManager channelManager;

    public AbstractSessionManager() {
        sessionMap = new ConcurrentHashMap<>();
        channelManager = DefaultChannelManager.getInstance();
    }

    public abstract Session newSession(String sessionId);

    @Override
    public Session newSession() {
        String sessionId = IdUtil.objectId();
        Session session = newSession(sessionId);
        sessionMap.putIfAbsent(sessionId, session);
        return session;
    }

    @Override
    public void bound(Session session, ChannelId channelId) {
        Assert.notNull(session, "session can not be null");
        Assert.notNull(channelId, "channelId can not be null");
        ChannelWrapper channelWrapper = channelManager.getChannelWrapper(channelId);
        Assert.notNull(channelWrapper, "channelId does not exists");
        session.bound(channelId, channelWrapper.getChannelType());
    }

    @Override
    public void removeSession(ChannelId channelId) {
        Assert.notNull(channelId, "channelId can not be null");
        Collection<Session> sessions = getAllSessions();
        if (CollectionUtil.isEmpty(sessions)) {
            return;
        }
        Iterator<Session> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            if (session.channelId() == channelId) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public Session getSession(String sessionId) {
        Assert.notNull(sessionId, "sessionId can not be null");
        return sessionMap.get(sessionId);
    }

    @Override
    public Collection<Session> getAllSessions() {
        return sessionMap.values();
    }

}
