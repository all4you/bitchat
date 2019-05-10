package io.bitchat.transport;

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
public class PongPacket extends AbstractPacket {

    @Override
    public int symbol() {
        return PacketSymbols.PONG_PACKET;
    }

}
