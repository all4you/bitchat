package io.bitchat.protocol.packet;


import io.bitchat.protocol.serialize.SerializeAlgorithm;

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
