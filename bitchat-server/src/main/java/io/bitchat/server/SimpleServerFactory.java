package io.bitchat.server;

import cn.hutool.core.lang.Singleton;
import io.bitchat.router.RouterServerAttr;

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
        currentServer = Singleton.get(StandaloneServer.class, serverPort);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server newServer(RouterServerAttr routerServerAttr, Integer serverPort) {
        currentServer = Singleton.get(ClusterServer.class, routerServerAttr, serverPort);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server currentServer() {
        return currentServer;
    }

}
