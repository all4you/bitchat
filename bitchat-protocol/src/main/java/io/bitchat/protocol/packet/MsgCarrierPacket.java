package io.bitchat.protocol.packet;

import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.protocol.packet.AbstractPacket;
import lombok.*;

/**
 * <p>
 * This Packet only carries some code or message
 * No need to handle it and don't response back either
 * </p>
 *
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MsgCarrierPacket<T> extends AbstractPacket {

    private int code;

    private boolean success;

    private T msg;

    @Override
    public int symbol() {
        return PacketSymbols.MSG_CARRIER_PACKET;
    }

}

