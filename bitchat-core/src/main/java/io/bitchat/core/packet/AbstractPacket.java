package io.bitchat.core.packet;


import io.bitchat.core.serialize.SerializeAlgorithm;

/**
 * @author houyi
 */
public abstract class AbstractPacket extends Packet {

    @Override
    public byte algorithm() {
        return SerializeAlgorithm.PROTO_STUFF.getType();
    }

    @Override
    public byte async() {
        return AsyncHandle.ASYNC;
    }

}
