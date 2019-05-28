package io.bitchat.protocol.packet;

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
public class CarrierPacket<T> extends AbstractPacket {

    private int code;

    private boolean success;

    private String msg;

    private T data;

    @Override
    public int symbol() {
        return ReservedSymbols.CARRIER_PACKET;
    }

    public static CarrierPacket<String> getStringCarrierPacket(boolean success, String msg, String data) {
        return CarrierPacket.<String>builder().success(success).msg(msg).data(data).build();
    }

}

