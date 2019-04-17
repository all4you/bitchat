package io.bitchat.core.server;

import io.bitchat.core.Node;

/**
 * <p>
 * A server node
 * </p>
 *
 * @author houyi
 */
public interface Server extends Node {

    /**
     * return the attribute of current server
     *
     * @return the attribute of the server
     */
    ServerAttr attribute();

    /**
     * start the server
     */
    void start();

    /**
     * stop the server
     */
    void stop();

    /**
     * register to a router
     */
    void registerToRouter();

}
