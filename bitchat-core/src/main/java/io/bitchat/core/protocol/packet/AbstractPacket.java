package io.bitchat.core.protocol.packet;

import io.bitchat.core.lang.enums.SerializeAlgorithm;

/**
 * @author houyi
 */
public abstract class AbstractPacket extends Packet {

    @Override
    public byte algorithm() {
        return SerializeAlgorithm.PROTO_STUFF.getType();
    }

}
