package io.bitchat.server;

/**
 * @author houyi
 */
public class ServerApplication {

    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.start(8864);
    }

}
