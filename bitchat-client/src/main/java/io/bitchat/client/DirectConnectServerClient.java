package io.bitchat.client;

import io.bitchat.core.server.ServerAttr;

/**
 * <p>
 * A Client which direct connect to server
 * </p>
 * @author houyi
 */
public class DirectConnectServerClient extends AbstractClient {

    public DirectConnectServerClient(ServerAttr serverAttr) {
        super(serverAttr);
    }

}
