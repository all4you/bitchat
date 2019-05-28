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
public class LoginRequestPacket extends AbstractPacket {

    private String userName;

    private String password;

    @Override
    public int symbol() {
        return PacketSymbols.LOGIN_REQUEST_PACKET;
    }

}
