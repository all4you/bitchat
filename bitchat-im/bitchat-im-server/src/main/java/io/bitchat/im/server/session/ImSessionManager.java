package io.bitchat.im.server.session;

import cn.hutool.core.lang.Singleton;
import io.bitchat.server.session.AbstractSessionManager;
import io.bitchat.server.session.SessionManager;

/**
 * @author houyi
 */
public class ImSessionManager extends AbstractSessionManager {

    private ImSessionManager() {

    }

    public static SessionManager getInstance() {
        return Singleton.get(ImSessionManager.class);
    }

    @Override
    public ImSession newSession(String sessionId) {
        return new ImSession(sessionId);
    }


}
