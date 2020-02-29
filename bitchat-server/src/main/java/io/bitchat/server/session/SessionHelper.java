package io.bitchat.server.session;

import io.bitchat.server.AttributeKeys;
import io.netty.channel.Channel;

/**
 * @author houyi
 */
public class SessionHelper {

    private SessionHelper() {

    }

    /**
     * mark this channel with attribute sessionId
     *
     * @param channel the channel
     */
    public static void markOnline(Channel channel, String sessionId) {
        channel.attr(AttributeKeys.SESSION_ID).set(sessionId);
    }

    /**
     * mark this channel with attribute sessionId
     *
     * @param channel the channel
     */
    public static void markOffline(Channel channel) {
        channel.attr(AttributeKeys.SESSION_ID).set(null);
    }

    /**
     * check whether the channel is login
     *
     * @param channel the channel
     * @return true if logged in otherwise false
     */
    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(AttributeKeys.SESSION_ID) && channel.attr(AttributeKeys.SESSION_ID).get() != null;
    }

}
