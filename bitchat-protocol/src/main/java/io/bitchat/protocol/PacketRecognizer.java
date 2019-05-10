package io.bitchat.protocol;


import io.bitchat.protocol.packet.Packet;
import io.bitchat.protocol.packet.PacketHandler;

/**
 * <p>
 * A Packet Recognizer which can
 * recognize the detailed Packet
 * and PacketHandler by symbol
 * </p>
 *
 * @author houyi
 */
public interface PacketRecognizer {

    /**
     * get the actual Packet Class
     *
     * @param symbol the packet symbol
     * @return the detailed Packet Class
     */
    Class<? extends Packet> packet(int symbol);

    /**
     * get the detailed Packet Handler
     *
     * @param symbol the packet symbol
     * @return the detailed Packet Handler
     */
    PacketHandler packetHandler(int symbol);

}
