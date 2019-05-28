package io.bitchat.protocol.packet;

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
        return ReservedSymbols.PING_PACKET;
    }

    @Override
    public byte async() {
        // handle this packet in sync way
        return AsyncHandle.SYNC;
    }

}
