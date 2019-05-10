package io.bitchat.protocol.packet;

import io.bitchat.protocol.serialize.SerializeAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EchoPacket extends AbstractPacket {

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
