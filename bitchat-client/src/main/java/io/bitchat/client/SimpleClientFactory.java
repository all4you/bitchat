package io.bitchat.client;

import cn.hutool.core.lang.Singleton;
import io.bitchat.core.client.Client;
import io.bitchat.core.client.ClientFactory;
import io.bitchat.core.router.LoadBalancer;
import io.bitchat.core.server.ServerAttr;
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

    @Override
    public Client newClient(ServerAttr serverAttr) {
        return Singleton.get(DirectConnectServerClient.class, serverAttr);
    }

    @Override
    public Client newClient(LoadBalancer loadBalancer) {
        return Singleton.get(ConnectServerByRouterClient.class, loadBalancer);
    }

}
