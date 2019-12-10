package io.bitchat.packet;


import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.serialize.SerializeAlgorithm;
import lombok.NoArgsConstructor;

/**
 * <p>
 * the only implement of {@link Packet}
 * user can create a packet by {@link PacketFactory}
 * </p>
 *
 * @author houyi
 */
@NoArgsConstructor
public class DefaultPacket extends Packet {

    @Override
    public byte algorithm() {
        return SerializeAlgorithm.PROTO_STUFF.getType();
    }

    @Override
    public boolean handleAsync() {
        return false;
    }

}
