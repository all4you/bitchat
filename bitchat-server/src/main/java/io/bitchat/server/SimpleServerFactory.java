package io.bitchat.server;

import cn.hutool.core.lang.Singleton;
import io.bitchat.core.router.RouterServerAttr;
import io.bitchat.core.server.Server;
import io.bitchat.core.server.ServerAttrHolder;
import io.bitchat.core.server.ServerFactory;

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
    public Server newServer(RouterServerAttr routerServerAttr, Integer serverPort) {
        currentServer = new ClusterServer(routerServerAttr, serverPort);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server currentServer() {
        return currentServer;
    }

}
