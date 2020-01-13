package io.bitchat.server.channel;

import cn.hutool.core.lang.Singleton;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class DefaultChannelListener implements ChannelListener {

    private ChannelManager channelManager;

    private DefaultChannelListener() {
        channelManager = DefaultChannelManager.getInstance();
    }

    public static ChannelListener getInstance() {
        return Singleton.get(DefaultChannelListener.class);
    }

    @Override
    public void channelActive(Channel channel, ChannelType channelType) {
        channelManager.addChannel(channel, channelType);
        log.info("Add a new Channel={}, channelType={}", channel, channelType);
    }

    @Override
    public void channelInactive(Channel channel) {
        channelManager.removeChannel(channel.id());
        log.info("Remove an inactive Channel={}", channel);
    }

}
