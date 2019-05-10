package io.bitchat.protocol.serialize;

import lombok.Getter;

import java.util.Arrays;

/**
 * <p>
 * A serialize algorithm enum
 * </p>
 *
 * @author houyi
 */
@Getter
public enum SerializeAlgorithm {

    /**
     * JDK
     */
    JDK((byte) 1),
    /**
     * fastJson
     */
    FAST_JSON((byte) 2),
    /**
     * hessian
     */
    HESSIAN((byte) 3),
    /**
     * kryo
     */
    KRYO((byte) 4),
    /**
     * protoStuff
     */
    PROTO_STUFF((byte) 5);

    private byte type;

    SerializeAlgorithm(byte type) {
        this.type = type;
    }

    /**
     * get the enum class
     *
     * @param type the type
     * @return the enum class
     */
    public static SerializeAlgorithm getEnum(Byte type) {
        return type == null ? null : Arrays.stream(values())
                .filter(t -> t.getType() == type)
                .findFirst()
                .orElse(null);
    }

}
