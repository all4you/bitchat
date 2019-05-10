package io.bitchat.server;

import io.bitchat.router.RouterServerAttr;

/**
 * <p>
 * A server factory
 * </p>
 *
 * @author houyi
 */
public interface ServerFactory {

    /**
     * create a standalone server
     *
     * @param serverPort the server port
     * @return the server
     */
    Server newServer(Integer serverPort);

    /**
     * create a cluster server
     *
     * @param routerServerAttr the router attr
     * @param serverPort       the server port
     * @return the server
     */
    Server newServer(RouterServerAttr routerServerAttr, Integer serverPort);

    /**
     * get current server
     *
     * @return current server
     */
    Server currentServer();

}
