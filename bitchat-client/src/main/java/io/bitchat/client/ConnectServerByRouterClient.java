package io.bitchat.client;

import io.bitchat.core.router.LoadBalancer;

/**
 * <p>
 * A Client which connect to server by
 * request the {@link io.bitchat.core.server.ServerAttr}
 * from a ${@link io.bitchat.core.router.LoadBalancer}
 * </p>
 *
 * @author houyi
 */
public class ConnectServerByRouterClient extends AbstractClient {

    public ConnectServerByRouterClient(LoadBalancer loadBalancer) {
        super(loadBalancer);
    }

}
