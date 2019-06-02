package io.bitchat.server;

/**
 * @author houyi
 */
public class ServerApplication {

    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channelListener(SimpleChannelListener.class)
                .start(8864);
    }

}
