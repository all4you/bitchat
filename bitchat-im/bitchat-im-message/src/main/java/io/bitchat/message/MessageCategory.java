package io.bitchat.message;

import lombok.Getter;

import java.util.Arrays;

/**
 * <p>
 * A message category enum
 * </p>
 *
 * @author houyi
 */
@Getter
public enum MessageCategory {

    /**
     * p2p
     */
    P2P((byte) 1),
    /**
     * group
     */
    GROUP((byte) 2);

    private byte type;

    MessageCategory(byte type) {
        this.type = type;
    }

    /**
     * get the enum class
     *
     * @param type the type
     * @return the enum class
     */
    public static MessageCategory getEnum(Byte type) {
        return type == null ? null : Arrays.stream(values())
                .filter(t -> t.getType() == type)
                .findFirst()
                .orElse(null);
    }

}
