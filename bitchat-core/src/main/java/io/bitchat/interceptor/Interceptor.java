package io.bitchat.interceptor;

import io.bitchat.protocol.Packet;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
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
