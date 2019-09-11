package io.bitchat.core.serialize;

import cn.hutool.core.lang.Singleton;

/**
 * <p>
 * A default Serializer Chooser implement
 * </p>
 *
 * @author houyi
 */
public class DefaultSerializerChooser implements SerializerChooser {

    private DefaultSerializerChooser() {

    }

    public static SerializerChooser getInstance() {
        return Singleton.get(DefaultSerializerChooser.class);
    }

    @Override
    public Serializer choose(byte serializeAlgorithm) {
        SerializeAlgorithm algorithm = SerializeAlgorithm.getEnum(serializeAlgorithm);
        switch (algorithm) {
            case JDK: {
                return JdkSerializer.getInstance();
            }
            case FAST_JSON: {
                return FastJsonSerializer.getInstance();
            }
            case HESSIAN: {
                return HessianSerializer.getInstance();
            }
            case KRYO: {
                return KryoSerializer.getInstance();
            }
            case PROTO_STUFF: {
                return ProtoStuffSerializer.getInstance();
            }
            default: {
                return null;
            }
        }
    }

}
