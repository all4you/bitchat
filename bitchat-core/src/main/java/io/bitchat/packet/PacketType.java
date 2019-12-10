package io.bitchat.packet;

/**
 * <p>
 * PacketType
 * </p>
 *
 * @author houyi
 */
public interface PacketType {

    /**
     * packet type: request
     */
    byte PACKET_TYPE_REQUEST = (byte) 1;

    /**
     * packet type: response
     */
    byte PACKET_TYPE_RESPONSE = (byte) 2;

    /**
     * packet type: command
     */
    byte PACKET_TYPE_COMMAND = (byte) 3;

}
