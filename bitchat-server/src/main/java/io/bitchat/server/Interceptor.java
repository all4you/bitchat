package io.bitchat.server;

import io.bitchat.core.Carrier;
import io.bitchat.protocol.packet.Packet;
import io.netty.channel.Channel;

/**
 * @author houyi
 **/
public abstract class Interceptor {

    public static final Carrier<Packet> SUCCESS = Carrier.<Packet>builder().success(true).build();

    /**
     * pre handle method of the interceptor
     *
     * @param channel the channel
     * @param packet  the packet
     * @return the response
     */
    public Carrier<Packet> preHandle(Channel channel, Packet packet) {
        return SUCCESS;
    }

}
