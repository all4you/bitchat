package io.bitchat.protocol.packet;

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
@AllArgsConstructor
public class P2pMsgRequestPacket extends AbstractPacket {

    private Long partnerId;

    private Byte messageType;

    private String msg;

    @Override
    public int symbol() {
        return PacketSymbols.P2P_MSG_REQUEST_PACKET;
    }

}
