package io.bitchat.message;

import java.util.List;

/**
 * <p>
 * A message reader which can fetch messages
 * </p>
 *
 * @author houyi
 */
public interface MessageReader {

    /**
     * fetch the history message
     * it can be p2p message or group message
     *
     * @param sessionId    the session id
     * @param currentMsgId current message id in the session
     * @param fetchForward whether fetch the message forward
     * @param fetchSize    the num of messages need to be fetched
     * @return the message list
     */
    List<Message> fetchHistoryMsg(Long sessionId, Long currentMsgId, boolean fetchForward, Integer fetchSize);

    /**
     * fetch the unread message of specified user
     *
     * @param userId the specified user id
     * @return the message list
     */
    List<Message> fetchOfflineMsg(Long userId);

}
