package io.bitchat.message;

import lombok.Getter;

import java.util.Arrays;

/**
 * <p>
 * A message type enum
 * </p>
 *
 * @author houyi
 */
@Getter
public enum MessageType {

    /**
     * text
     */
    TEXT((byte) 1),
    /**
     * image
     */
    IMAGE((byte) 2),
    /**
     * emoji
     */
    EMOJI((byte) 3),
    /**
     * video
     */
    VIDEO((byte) 4),
    /**
     * link
     */
    LINK((byte) 5);

    private byte type;

    MessageType(byte type) {
        this.type = type;
    }

    /**
     * get the enum class
     *
     * @param type the type
     * @return the enum class
     */
    public static MessageType getEnum(Byte type) {
        return type == null ? null : Arrays.stream(values())
                .filter(t -> t.getType() == type)
                .findFirst()
                .orElse(null);
    }

}
