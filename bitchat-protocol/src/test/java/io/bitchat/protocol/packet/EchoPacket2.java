package io.bitchat.protocol.packet;

import io.bitchat.core.lang.enums.SerializeAlgorithm;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.protocol.packet.handler.EchoPacketHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * A same symbol with {@link EchoPacket}
 * Only one Packet will be stored
 * </p>
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EchoPacket2 extends Packet {

    private String msg;

    @Override
    public byte algorithm() {
        return SerializeAlgorithm.PROTO_STUFF.getType();
    }

    @Override
    public int symbol() {
        return -1;
    }

}
