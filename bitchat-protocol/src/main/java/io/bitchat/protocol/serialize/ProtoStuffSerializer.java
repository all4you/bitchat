package io.bitchat.protocol.serialize;

import cn.hutool.core.lang.Singleton;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author houyi
 */
public class ProtoStuffSerializer implements Serializer {

    private final Object CACHE_LOCK = new Object();

    private Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private Objenesis objenesis = new ObjenesisStd(true);

    private ProtoStuffSerializer() {

    }

    public static Serializer getInstance() {
        return Singleton.get(ProtoStuffSerializer.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public byte[] serialize(Object object) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] bytes = null;
        try {
            Class clazz = object.getClass();
            Schema schema = getSchema(clazz);
            bytes = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T object = null;
        try {
            object = objenesis.newInstance(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, object, schema);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            synchronized (CACHE_LOCK) {
                schema = (Schema<T>) cachedSchema.get(clazz);
                if (schema == null) {
                    schema = RuntimeSchema.getSchema(clazz);
                    cachedSchema.putIfAbsent(clazz, schema);
                }
            }
        }
        return schema;
    }

}
