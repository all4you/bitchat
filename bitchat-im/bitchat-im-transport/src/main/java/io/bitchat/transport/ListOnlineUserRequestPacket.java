package io.bitchat.transport;

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
public class ListOnlineUserRequestPacket extends AbstractPacket {

    @Override
    public int symbol() {
        return PacketSymbols.LIST_ONLINE_USER_REQUEST_PACKET;
    }

}
