package io.bitchat.im.client;

import io.bitchat.client.Client;
import io.bitchat.client.SimpleClientFactory;
import io.bitchat.core.ServerAttr;

/**
 * @author houyi
 */
public class ClientApplication {

    public static void main(String[] args) {
        Client client = SimpleClientFactory.getInstance()
                .newClient(ServerAttr.getLocalServer(8864));
        client.connect();

        ClientService clientService = new ClientService(client);
        clientService.doCli();
    }

}
