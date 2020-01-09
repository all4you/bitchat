package io.bitchat.im.server;

import cn.hutool.core.lang.Singleton;
import io.bitchat.im.server.connection.ConnectionManager;
import io.bitchat.im.server.connection.DefaultConnectionManager;
import io.bitchat.server.channel.ChannelListener;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class SimpleChannelListener implements ChannelListener {

    private ConnectionManager connectionManager;

    private SimpleChannelListener() {
        this.connectionManager = DefaultConnectionManager.getInstance();
    }

    public static ChannelListener getInstance() {
        return Singleton.get(SimpleChannelListener.class);
    }

    @Override
    public void channelActive(Channel channel) {
        // do nothing
    }

    @Override
    public void channelInactive(Channel channel) {
        connectionManager.remove(channel);
        log.info("Has removed the channel:{}", channel);
    }

}
