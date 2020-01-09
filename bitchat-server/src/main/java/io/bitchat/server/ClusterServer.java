package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import io.bitchat.router.RouterServerAttr;
import io.bitchat.server.channel.ChannelListener;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * A cluster server
 * </p>
 *
 * @author houyi
 */
@Slf4j
public class ClusterServer extends AbstractServer {

    private RouterServerAttr routerServerAttr;

    public ClusterServer(Integer serverPort, RouterServerAttr routerServerAttr) {
        this(serverPort, null, routerServerAttr);
    }

    public ClusterServer(Integer serverPort, ChannelListener channelListener, RouterServerAttr routerServerAttr) {
        super(serverPort, channelListener);
        Assert.notNull(routerServerAttr, "routerServerAttr can not be null");
        this.routerServerAttr = routerServerAttr;
    }

    @Override
    public void registerToRouter() {
        // register to router
    }

}
