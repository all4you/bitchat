package io.bitchat.server.channel;

import io.netty.channel.Channel;

/**
 * @author houyi
 */
public interface ChannelListener {

    void channelActive(Channel channel);

    void channelInactive(Channel channel);
}
