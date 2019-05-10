package io.bitchat.protocol.serialize;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import com.alibaba.fastjson.JSON;

/**
 * @author houyi
 */
public class FastJsonSerializer implements Serializer {

    private FastJsonSerializer() {

    }

    public static Serializer getInstance() {
        return Singleton.get(FastJsonSerializer.class);
    }

    @Override
    public byte[] serialize(Object object) {
        Assert.notNull(object, "the serialize object can not be null");
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Assert.notNull(bytes, "the deserialize bytes can not be null");
        return JSON.parseObject(bytes, clazz);
    }

}
