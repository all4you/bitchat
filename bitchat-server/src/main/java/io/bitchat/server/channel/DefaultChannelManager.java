package io.bitchat.server.channel;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author houyi
 */
@Slf4j
public class DefaultChannelManager implements ChannelManager {

    private ChannelGroup channels;

    private Map<ChannelId, ChannelType> channelTypeMap;

    private DefaultChannelManager() {
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelTypeMap = new ConcurrentHashMap<>();
    }

    public static ChannelManager getInstance() {
        return Singleton.get(DefaultChannelManager.class);
    }

    @Override
    public void addChannel(Channel channel, ChannelType channelType) {
        Assert.notNull(channel, "channel can not be null");
        Assert.notNull(channelType, "channelType can not be null");
        channels.add(channel);
        channelTypeMap.putIfAbsent(channel.id(), channelType);
    }

    @Override
    public void removeChannel(ChannelId channelId) {
        Assert.notNull(channelId, "channelId can not be null");
        channels.remove(channelId);
        channelTypeMap.remove(channelId);
    }

    @Override
    public ChannelWrapper getChannelWrapper(ChannelId channelId) {
        Assert.notNull(channelId, "channelId can not be null");
        if (channels.isEmpty()) {
            return null;
        }
        return ChannelWrapper.builder()
                .channel(channels.find(channelId))
                .channelType(channelTypeMap.get(channelId))
                .build();
    }

    @Override
    public ChannelWrapper getChannelWrapper(String longId) {
        Assert.notNull(longId, "longId can not be null");
        if (channels.isEmpty()) {
            return null;
        }
        ChannelId channelId = channels.stream()
                .filter(channel -> channel.id().asLongText().equals(longId))
                .map(Channel::id)
                .findFirst()
                .orElse(null);
        return channelId == null ? null : getChannelWrapper(channelId);
    }

    @Override
    public List<ChannelWrapper> getAllChannelWrappers() {
        if (channels.isEmpty()) {
            return Collections.emptyList();
        }
        return channels.stream()
                .map(channel -> ChannelWrapper.builder()
                        .channel(channel)
                        .channelType(channelTypeMap.get(channel.id()))
                        .build())
                .collect(Collectors.toList());
    }
}
