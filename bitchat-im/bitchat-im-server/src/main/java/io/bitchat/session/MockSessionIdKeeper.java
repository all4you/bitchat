package io.bitchat.session;

import io.bitchat.core.bean.Bean;
import io.bitchat.server.SessionIdKeeper;

/**
 * @author houyi
 */
@Bean
public class MockSessionIdKeeper implements SessionIdKeeper {

    @Override
    public Long p2pSessionId(Long userId, Long partnerId) {
        return 1L;
    }

    @Override
    public Long groupSessionId(Long groupId) {
        return 2L;
    }

}
