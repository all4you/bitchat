package io.bitchat.client;

import cn.hutool.core.lang.Singleton;
import io.bitchat.core.LoadBalancer;
import io.bitchat.core.ServerAttr;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class SimpleClientFactory implements ClientFactory {

    private SimpleClientFactory() {

    }

    public static ClientFactory getInstance() {
        return Singleton.get(SimpleClientFactory.class);
    }

    /**
     * <p>
     * A Client which connect to server directly
     * </p>
     * @author houyi
     */
    @Override
    public Client newClient(ServerAttr serverAttr) {
        return Singleton.get(GenericClient.class, serverAttr);
    }

    /**
     * <p>
     * A Client which connect to server by
     * request the {@link io.bitchat.core.ServerAttr}
     * from a ${@link io.bitchat.core.LoadBalancer}
     * </p>
     * @param loadBalancer the load balancer
     */
    @Override
    public Client newClient(LoadBalancer loadBalancer) {
        return Singleton.get(GenericClient.class, loadBalancer);
    }

}
