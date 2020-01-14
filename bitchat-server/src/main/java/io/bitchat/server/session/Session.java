package io.bitchat.server.session;

import io.bitchat.server.channel.ChannelType;
import io.netty.channel.ChannelId;

/**
 * A Session is bound with an unique Channel
 *
 * @author houyi
 */
public interface Session {

    String sessionId();

    /**
     * bound the session with an unique channel
     */
    void bound(ChannelId channelId, long userId);

    long userId();

    ChannelId channelId();

    ChannelType channelType();

    void writeAndFlush(Object msg);

}
