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
@AllArgsConstructor
public class RegisterRequestPacket extends AbstractPacket {

    private String userName;

    private String password;

    @Override
    public int symbol() {
        return PacketSymbols.REGISTER_REQUEST_PACKET;
    }

}
