package io.bitchat.im.message;

import cn.hutool.core.lang.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class DefaultMessageWriter implements MessageWriter {

    private DefaultMessageWriter() {

    }

    public static DefaultMessageWriter getInstance() {
        return Singleton.get(DefaultMessageWriter.class);
    }


    @Override
    public void saveHistoryMsg(Message message) {
        log.info("saveHistoryMsg with message={} successfully", message);
    }

    @Override
    public void saveOfflineMsg(Message message, Long offlineUserId) {
        log.info("saveOfflineMsg with message={} successfully", message);
    }

}
