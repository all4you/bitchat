package io.bitchat.server.session;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
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

    private static Map<String, Session> sessionMap;

    public AbstractSessionManager() {
        sessionMap = new ConcurrentHashMap<>();
    }

    public abstract Session newSession(String sessionId);

    @Override
    public Session newSession() {
        String sessionId = IdUtil.objectId();
        return newSession(sessionId);
    }

    @Override
    public void bound(Session session, ChannelId channelId) {
        Assert.notNull(session, "session can not be null");
        Assert.notNull(channelId, "channelId can not be null");
        session.bound(channelId);
        sessionMap.putIfAbsent(session.sessionId(), session);
        log.info("bound a new session, session={}, channelId={}", session, channelId);
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
                log.info("remove a session, session={}, channelId={}", session, channelId);
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
