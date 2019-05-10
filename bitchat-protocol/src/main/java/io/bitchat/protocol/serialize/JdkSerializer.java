package io.bitchat.protocol.serialize;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ObjectUtil;

/**
 * @author houyi
 */
public class JdkSerializer implements Serializer {

    private JdkSerializer() {

    }

    public static Serializer getInstance() {
        return Singleton.get(JdkSerializer.class);
    }

    @Override
    public byte[] serialize(Object object) {
        Assert.notNull(object, "the serialize object can not be null");
        return ObjectUtil.serialize(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Assert.notNull(bytes, "the deserialize bytes can not be null");
        return ObjectUtil.unserialize(bytes);
    }

}
