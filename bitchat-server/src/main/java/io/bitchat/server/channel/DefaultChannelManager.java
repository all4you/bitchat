package io.bitchat.server.channel;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author houyi
 */
@Slf4j
public class DefaultChannelManager implements ChannelManager {

    private static final AttributeKey<ChannelType> CHANNEL_TYPE = AttributeKey.newInstance("channelType");

    private ChannelGroup channels;

    private DefaultChannelManager() {
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public static ChannelManager getInstance() {
        return Singleton.get(DefaultChannelManager.class);
    }

    @Override
    public void addChannel(Channel channel, ChannelType channelType) {
        Assert.notNull(channel, "channel can not be null");
        Assert.notNull(channelType, "channelType can not be null");
        channel.attr(CHANNEL_TYPE).set(channelType);
        channels.add(channel);
    }

    @Override
    public void removeChannel(ChannelId channelId) {
        Assert.notNull(channelId, "channelId can not be null");
        channels.remove(channelId);
    }

    @Override
    public ChannelWrapper getChannelWrapper(ChannelId channelId) {
        Assert.notNull(channelId, "channelId can not be null");
        if (channels.isEmpty()) {
            return null;
        }
        Channel channel = channels.find(channelId);
        return wrapChannel(channel);
    }

    @Override
    public ChannelWrapper getChannelWrapper(String longId) {
        Assert.notNull(longId, "longId can not be null");
        if (channels.isEmpty()) {
            return null;
        }
        Channel channel = channels.stream()
                .filter(item -> item.id().asLongText().equals(longId))
                .findFirst()
                .orElse(null);
        return wrapChannel(channel);
    }

    @Override
    public List<ChannelWrapper> getAllChannelWrappers() {
        if (channels.isEmpty()) {
            return Collections.emptyList();
        }
        return channels.stream()
                .map(this::wrapChannel)
                .collect(Collectors.toList());
    }

    private ChannelWrapper wrapChannel(Channel channel) {
        return channel == null ? null : ChannelWrapper.builder()
                .channel(channel)
                .channelType(channel.attr(CHANNEL_TYPE).get())
                .build();
    }

}
