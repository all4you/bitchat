package io.bitchat.server;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * A standalone server
 * </p>
 *
 * @author houyi
 */
@Slf4j
public class StandaloneServer extends AbstractServer {

    public StandaloneServer(Integer serverPort) {
        super(serverPort);
    }

    @Override
    public void registerToRouter() {
        // do nothing
    }

}
