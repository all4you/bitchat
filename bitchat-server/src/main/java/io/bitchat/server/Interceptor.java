package io.bitchat.server;

import io.bitchat.core.Carrier;
import io.bitchat.protocol.packet.Packet;

/**
 * @author houyi
 **/
public abstract class Interceptor {

    public static final Carrier<Packet> SUCCESS = Carrier.<Packet>builder().success(true).build();

    /**
     * pre handle method of the interceptor
     *
     * @param packet the packet
     * @return the response
     */
    public Carrier<Packet> preHandle(Packet packet) {
        return SUCCESS;
    }

    /**
     * pre handle method of the interceptor
     *
     * @param packet the packet
     */
    @Deprecated
    public abstract void postHandle(Packet packet);

}
