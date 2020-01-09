package io.bitchat.server;

import cn.hutool.core.lang.Singleton;
import io.bitchat.router.RouterServerAttr;
import io.bitchat.server.channel.ChannelListener;

/**
 * @author houyi
 */
public class SimpleServerFactory implements ServerFactory {

    private Server currentServer;

    private SimpleServerFactory() {

    }

    public static ServerFactory getInstance() {
        return Singleton.get(SimpleServerFactory.class);
    }

    @Override
    public Server newServer(Integer serverPort) {
        currentServer = new StandaloneServer(serverPort);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server newServer(Integer serverPort, ChannelListener channelListener) {
        currentServer = new StandaloneServer(serverPort, channelListener);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server newClusterServer(Integer serverPort, RouterServerAttr routerServerAttr) {
        currentServer = new ClusterServer(serverPort, routerServerAttr);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server newClusterServer(Integer serverPort, ChannelListener channelListener, RouterServerAttr routerServerAttr) {
        currentServer = new ClusterServer(serverPort, channelListener, routerServerAttr);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server currentServer() {
        return currentServer;
    }

}
