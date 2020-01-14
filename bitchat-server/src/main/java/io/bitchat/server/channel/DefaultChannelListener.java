package io.bitchat.server.channel;

import cn.hutool.core.lang.Singleton;
import io.bitchat.server.session.DefaultSessionManager;
import io.bitchat.server.session.SessionHelper;
import io.bitchat.server.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class DefaultChannelListener implements ChannelListener {

    private ChannelManager channelManager;
    private SessionManager sessionManager;

    private DefaultChannelListener() {
        channelManager = DefaultChannelManager.getInstance();
        sessionManager = DefaultSessionManager.getInstance();
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
        ChannelId channelId = channel.id();
        channelManager.removeChannel(channelId);
        sessionManager.removeSession(channelId);
        SessionHelper.markOffline(channel);
        log.info("Remove an inactive Channel={}", channel);
    }

}
