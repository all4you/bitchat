package io.bitchat.client;

import io.bitchat.core.LoadBalancer;
import io.bitchat.core.ServerAttr;

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
    Client newBalancedClient(LoadBalancer loadBalancer);

}
