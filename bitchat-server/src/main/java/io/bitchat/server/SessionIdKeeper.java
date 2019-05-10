package io.bitchat.server;

/**
 * <p>
 * A session id keeper aim to get the session id of p2p or group chat
 * </p>
 *
 * @author houyi
 */
public interface SessionIdKeeper {

    /**
     * return the session id of p2p chat
     *
     * @param userId    one user id
     * @param partnerId another user id
     * @return the session id
     */
    Long p2pSessionId(Long userId, Long partnerId);

    /**
     * return the session id of group chat
     *
     * @param groupId group id
     * @return the session id
     */
    Long groupSessionId(Long groupId);

}
