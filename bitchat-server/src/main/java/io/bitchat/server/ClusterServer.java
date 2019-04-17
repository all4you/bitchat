package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.router.RouterServerAttr;
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

    public ClusterServer(RouterServerAttr routerServerAttr, int serverPort) {
        super(serverPort);
        Assert.notNull(routerServerAttr, "routerServerAttr can not be null");
        this.routerServerAttr = routerServerAttr;
    }

    @Override
    public void registerToRouter() {
        // register to router
    }

}
