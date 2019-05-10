package io.bitchat.transport;

import io.bitchat.protocol.packet.AsyncHandle;
import io.bitchat.protocol.packet.PacketSymbols;
import io.bitchat.protocol.packet.AbstractPacket;
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
