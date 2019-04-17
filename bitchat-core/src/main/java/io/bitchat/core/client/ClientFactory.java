package io.bitchat.core.client;

import io.bitchat.core.router.LoadBalancer;
import io.bitchat.core.server.ServerAttr;

/**
 * @author houyi
 */
public interface ClientFactory {

    /**
     * create a {@link Client} with {@link ServerAttr}
     *
     * @param serverAttr the serverAttr
     * @return a {@link Client}
     */
    Client newClient(ServerAttr serverAttr);

    /**
     * create a {@link Client} by {@link LoadBalancer}
     *
     * @param loadBalancer the load balancer
     * @return a {@link Client}
     */
    Client newClient(LoadBalancer loadBalancer);

}
