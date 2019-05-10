package io.bitchat.server;

/**
 * @author houyi
 */
public class StandaloneServerApplication {

    public static void main(String[] args) {
        Server server = SimpleServerFactory.getInstance().newServer(8864);
        server.start();
    }

}
