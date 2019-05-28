package io.bitchat.connection;

import io.netty.channel.Channel;

/**
 * @author houyi
 */
public class ConnectionUtil {

    private ConnectionUtil() {

    }

    /**
     * mark this channel with attribute online=true
     *
     * @param channel the channel
     */
    public static void markOnline(Channel channel) {
        channel.attr(Attributes.ON_LINE).set(true);
    }

    /**
     * mark this channel with attribute online=null
     *
     * @param channel the channel
     */
    public static void markOffline(Channel channel) {
        channel.attr(Attributes.ON_LINE).set(null);
    }

    /**
     * check whether the channel is login
     *
     * @param channel the channel
     * @return true if logged in otherwise false
     */
    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.ON_LINE) && channel.attr(Attributes.ON_LINE).get();
    }

}
