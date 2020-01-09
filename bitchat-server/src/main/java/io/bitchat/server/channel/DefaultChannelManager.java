package io.bitchat.server.channel;

import cn.hutool.core.lang.Singleton;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class DefaultChannelManager implements ChannelManager {

    private ChannelGroup channels;

    private DefaultChannelManager() {
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public static ChannelManager getInstance() {
        return Singleton.get(DefaultChannelManager.class);
    }

    @Override
    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    @Override
    public void removeChannel(ChannelId id) {
        channels.remove(id);
    }

    @Override
    public Channel getChannel(ChannelId id) {
        return channels.find(id);
    }


}
