package io.bitchat.server;

import io.bitchat.connection.ConnectionManager;
import io.bitchat.core.bean.Autowired;
import io.bitchat.core.bean.Bean;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
@Bean
public class SimpleChannelListener implements ChannelListener {

    private ConnectionManager connectionManager;

    @Autowired
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
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
