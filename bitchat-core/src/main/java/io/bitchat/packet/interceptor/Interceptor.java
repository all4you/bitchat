package io.bitchat.packet.interceptor;

import io.bitchat.packet.Packet;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.PayloadFactory;
import io.netty.channel.Channel;

/**
 * <p>
 * A pre handle interceptor
 * </p>
 *
 * @author houyi
 **/
public abstract class Interceptor {

    /**
     * pre handle method of the interceptor
     *
     * @param channel the channel
     * @param packet  the packet
     * @return the response
     */
    public Payload preHandle(Channel channel, Packet packet) {
        return PayloadFactory.newSuccessPayload();
    }

}
