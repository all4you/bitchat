package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.router.RouterServerAttr;

/**
 * <p>
 * A server bootstrap
 * </p>
 *
 * @author houyi
 */
public class ServerBootstrap {

    private ServerMode serverMode = ServerMode.STAND_ALONE;

    private RouterServerAttr routerServerAttr;

    private ChannelListener channelListener;

    public ServerBootstrap serverMode(ServerMode serverMode) {
        this.serverMode = serverMode == null ? ServerMode.STAND_ALONE : serverMode;
        return this;
    }

    public ServerBootstrap routerServerAttr(RouterServerAttr routerServerAttr) {
        if (ServerMode.CLUSTER == serverMode) {
            Assert.notNull(routerServerAttr, "routerServerAttr can not be null");
            Assert.isTrue(routerServerAttr.valid(), "routerServerAttr is invalid");
            this.routerServerAttr = routerServerAttr;
        }
        return this;
    }

    public ServerBootstrap channelListener(Class<? extends ChannelListener> channelListener) {
        this.channelListener = Singleton.get(channelListener);
        return this;
    }

    public void start(Integer serverPort) {
        ServerFactory factory = SimpleServerFactory.getInstance();
        Server server;
        if (ServerMode.STAND_ALONE == serverMode) {
            server = factory.newServer(serverPort, channelListener);
        } else {
            Assert.notNull(routerServerAttr, "routerServerAttr can not be null cause you are starting the server in cluster mode");
            Assert.isTrue(routerServerAttr.valid(), "routerServerAttr is invalid");
            server = factory.newClusterServer(serverPort, channelListener, routerServerAttr);
        }
        server.start();
    }

}
