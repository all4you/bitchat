package io.bitchat.im.server.session;

import cn.hutool.core.lang.Singleton;
import io.bitchat.server.SessionIdKeeper;

/**
 * @author houyi
 */
public class DefaultSessionIdKeeper implements SessionIdKeeper {

    private DefaultSessionIdKeeper() {

    }

    public static SessionIdKeeper getInstance() {
        return Singleton.get(DefaultSessionIdKeeper.class);
    }


    @Override
    public Long p2pSessionId(Long userId, Long partnerId) {
        return 1L;
    }

    @Override
    public Long groupSessionId(Long groupId) {
        return 2L;
    }

}
