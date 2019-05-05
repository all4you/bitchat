package io.bitchat.protocol.packet;

import io.bitchat.core.lang.constants.AsyncHandle;
import io.bitchat.core.lang.constants.PacketSymbols;
import io.bitchat.core.protocol.packet.AbstractPacket;
import lombok.*;

/**
 * @author houyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
public class PingPacket extends AbstractPacket {

    @Override
    public int symbol() {
        return PacketSymbols.PING_PACKET;
    }

    @Override
    public byte async() {
        // handle this packet in sync way
        return AsyncHandle.SYNC;
    }

}
