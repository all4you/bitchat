package io.bitchat.server;

import io.bitchat.core.Listener;
import io.netty.channel.Channel;

/**
 * @author houyi
 */
public abstract class ChannelListener implements Listener<Channel> {

    @Override
    public void onEvent(Channel event) {
        channelInactive(event);
    }

    public abstract void channelInactive(Channel channel);
}
