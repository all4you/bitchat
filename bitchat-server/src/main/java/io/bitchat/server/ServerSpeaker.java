package io.bitchat.server;

import io.bitchat.core.ServerAttr;
import io.bitchat.protocol.packet.Packet;

/**
 * <p>
 * A Server Speaker will transmit command between two servers
 * </p>
 *
 * @author houyi
 */
public interface ServerSpeaker {

    /**
     * transmit a command to another Server
     *
     * @param serverAttr the server attr
     * @param packet     the request packet
     * @return the response packet
     */
    Packet speak(ServerAttr serverAttr, Packet packet);

}
