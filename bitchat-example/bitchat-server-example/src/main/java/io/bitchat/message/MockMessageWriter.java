package io.bitchat.message;

import io.bitchat.core.bean.Bean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@Bean
public class MockMessageWriter implements MessageWriter {

    @Override
    public void saveHistoryMsg(Message message) {
        log.info("saveHistoryMsg with message={} successfully", message);
    }

    @Override
    public void saveOfflineMsg(Message message, Long offlineUserId) {
        log.info("saveOfflineMsg with message={} successfully", message);
    }

}
