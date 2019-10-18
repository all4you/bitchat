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
     * create a standalone server
     *
     * @param serverPort      the server port
     * @param channelListener the channelListener
     * @return the server
     */
    Server newServer(Integer serverPort, ChannelListener channelListener);

    /**
     * create a cluster server
     *
     * @param serverPort       the server port
     * @param routerServerAttr the router attr
     * @return the server
     */
    Server newClusterServer(Integer serverPort, RouterServerAttr routerServerAttr);

    /**
     * create a cluster server
     *
     * @param serverPort       the server port
     * @param channelListener  the channelListener
     * @param routerServerAttr the router attr
     * @return the server
     */
    Server newClusterServer(Integer serverPort, ChannelListener channelListener, RouterServerAttr routerServerAttr);

    /**
     * get current server
     *
     * @return current server
     */
    Server currentServer();

}
