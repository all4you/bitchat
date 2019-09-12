package io.bitchat.core.protocol;

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
        return ReservedSymbols.PONG_PACKET;
    }

}
