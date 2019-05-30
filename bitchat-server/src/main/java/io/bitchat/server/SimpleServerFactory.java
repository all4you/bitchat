package io.bitchat.server;

import cn.hutool.core.lang.Singleton;
import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.SerializerChooser;
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
        currentServer = new StandaloneServer(serverPort);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server newServer(Integer serverPort, SerializerChooser chooser, PacketRecognizer recognizer, ChannelListener channelListener) {
        currentServer = new StandaloneServer(serverPort, chooser, recognizer, channelListener);
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
    public Server newClusterServer(Integer serverPort, SerializerChooser chooser, PacketRecognizer recognizer, ChannelListener channelListener, RouterServerAttr routerServerAttr) {
        currentServer = new ClusterServer(serverPort, chooser, recognizer, channelListener, routerServerAttr);
        ServerAttrHolder.put(currentServer.attribute());
        return currentServer;
    }

    @Override
    public Server currentServer() {
        return currentServer;
    }

}
