package io.bitchat.core.client;

import io.bitchat.core.Listener;
import io.bitchat.core.Carrier;
import io.bitchat.core.Node;
import io.bitchat.core.lang.enums.MessageType;
import io.bitchat.core.message.Message;

import java.util.List;

/**
 * <p>
 * A client which can communicate with Server
 * </p>
 *
 * @author houyi
 */
public interface Client extends Node {

    /**
     * connect to server
     */
    void connect();

    /**
     * user login
     *
     * @param userName userName
     * @param password password
     * @param listener listener
     */
    void login(String userName, String password, Listener<Carrier<String>> listener);

    /**
     * send a message to another person
     *
     * @param partnerId your partner user id
     * @param type      the message type
     * @param msg       the message detail
     * @param listener  listener
     */
    void sendP2pMsg(Long partnerId, MessageType type, String msg, Listener<Carrier<String>> listener);

    /**
     * send a message to a group
     *
     * @param groupId  the group id you send the message to
     * @param type     the message type
     * @param msg      the message detail
     * @param listener listener
     */
    void sendGroupMsg(Long groupId, MessageType type, String msg, Listener<Carrier<String>> listener);

    /**
     * fetch the p2p history message
     *
     * @param partnerId    your partner user id
     * @param currentMsgId current message id in the session
     * @param fetchForward whether fetch the message forward
     * @param fetchSize    the num of messages need to be fetched
     * @param listener     listener
     */
    void fetchP2pHistoryMsg(Long partnerId, Long currentMsgId, boolean fetchForward, Integer fetchSize, Listener<Carrier<List<Message>>> listener);

    /**
     * fetch the group history message
     *
     * @param groupId      the group id
     * @param currentMsgId current message id in the session
     * @param fetchForward whether fetch the message forward
     * @param fetchSize    the num of messages need to be fetched
     * @param listener     listener
     */
    void fetchGroupHistoryMsg(Long groupId, Long currentMsgId, boolean fetchForward, Integer fetchSize, Listener<Carrier<List<Message>>> listener);

    /**
     * fetch the unread message created when the specified user is offline
     *
     * @param listener listener
     */
    void fetchOfflineMsg(Listener<Carrier<List<Message>>> listener);

}
