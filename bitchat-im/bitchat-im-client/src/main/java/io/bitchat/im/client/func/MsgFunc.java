package io.bitchat.im.client.func;

import io.bitchat.im.BaseResult;
import io.bitchat.im.ListResult;
import io.bitchat.im.message.Message;
import io.bitchat.im.message.MessageType;

/**
 * @author houyi
 */
public interface MsgFunc {

    /**
     * send a message to another person
     *
     * @param partnerId your partner user id
     * @param type      the message type
     * @param msg       the message detail
     */
    BaseResult sendP2pMsg(Long partnerId, MessageType type, String msg);

    /**
     * send a message to a group
     *
     * @param groupId the group id you send the message to
     * @param type    the message type
     * @param msg     the message detail
     */
    BaseResult sendGroupMsg(Long groupId, MessageType type, String msg);

    /**
     * fetch the p2p history message
     *
     * @param partnerId    your partner user id
     * @param currentMsgId current message id in the session
     * @param fetchForward whether fetch the message forward
     * @param fetchSize    the num of messages need to be fetched
     */
    ListResult<Message> fetchP2pHistoryMsg(Long partnerId, Long currentMsgId, boolean fetchForward, Integer fetchSize);

    /**
     * fetch the group history message
     *
     * @param groupId      the group id
     * @param currentMsgId current message id in the session
     * @param fetchForward whether fetch the message forward
     * @param fetchSize    the num of messages need to be fetched
     */
    ListResult<Message> fetchGroupHistoryMsg(Long groupId, Long currentMsgId, boolean fetchForward, Integer fetchSize);

    /**
     * fetch the unread message created when the specified user is offline
     */
    ListResult<Message> fetchOfflineMsg();

}
