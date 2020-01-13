package io.bitchat.server.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.List;

/**
 * @author houyi
 */
public interface ChannelManager {

    /**
     * add the Channel when
     * {@link io.bitchat.server.ws.FrameHandler#channelActive(io.netty.channel.ChannelHandlerContext)}
     * is triggered
     */
    void addChannel(Channel channel, ChannelType channelType);

    /**
     * remove the Channel when
     * {@link io.bitchat.server.ws.FrameHandler#channelInactive(io.netty.channel.ChannelHandlerContext)}
     * is triggered
     */
    void removeChannel(ChannelId channelId);

    ChannelWrapper getChannelWrapper(ChannelId channelId);

    ChannelWrapper getChannelWrapper(String longId);

    List<ChannelWrapper> getAllChannelWrappers();

}
