package io.bitchat.connection;

import io.bitchat.user.User;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @author houyi
 */
public interface ConnectionManager {

    /**
     * add a connection with
     * the channel
     *
     * @param channel       the channel
     * @param user          the user
     * @param serverAddress the serverAddress
     * @param serverPort    the serverPort
     */
    void add(Channel channel, User user, String serverAddress, Integer serverPort);

    /**
     * remove the connection
     * by channel
     *
     * @param channel the channel
     */
    void remove(Channel channel);

    /**
     * get the connection by channel
     *
     * @param channel the channel
     * @return the connection
     */
    Connection get(Channel channel);

    /**
     * whether the channel is exists
     * in ConnectionManager
     *
     * @param channel the channel
     * @return true if channel is exists otherwise false
     */
    boolean contains(Channel channel);

    /**
     * get the connection set of a specified user
     *
     * @param userId the specified user id
     * @return the connection list
     */
    List<Connection> getList(Long userId);

    /**
     * get online user list
     *
     * @return the online user list
     */
    List<User> onlineUser();

}
