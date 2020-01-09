package io.bitchat.server.session;

import cn.hutool.core.lang.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class DefaultSessionManager extends AbstractSessionManager {

    private DefaultSessionManager() {

    }

    public static SessionManager getInstance() {
        return Singleton.get(DefaultSessionManager.class);
    }

    @Override
    public Session newSession(String sessionId) {
        return new DefaultSession(sessionId);
    }

}
