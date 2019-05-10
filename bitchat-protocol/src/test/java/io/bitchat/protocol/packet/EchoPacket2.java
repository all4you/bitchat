package io.bitchat.protocol.packet;

import io.bitchat.protocol.serialize.SerializeAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * A same symbol with {@link EchoPacket}
 * Only one Packet will be stored
 * </p>
 *
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EchoPacket2 extends AbstractPacket {

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
