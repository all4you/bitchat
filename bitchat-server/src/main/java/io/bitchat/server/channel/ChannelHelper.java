package io.bitchat.server.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

/**
 * @author houyi
 */
public class ChannelHelper {

    private static ChannelManager channelManager = DefaultChannelManager.getInstance();

    private ChannelHelper() {

    }

    public static ChannelType getChannelType(Channel channel) {
        ChannelWrapper channelWrapper = getChannelWrapper(channel);
        return channelWrapper.getChannelType();
    }

    public static ChannelWrapper getChannelWrapper(Channel channel) {
        return getChannelWrapper(channel.id());
    }

    public static ChannelWrapper getChannelWrapper(ChannelId channelId) {
        return channelManager.getChannelWrapper(channelId);
    }


}
