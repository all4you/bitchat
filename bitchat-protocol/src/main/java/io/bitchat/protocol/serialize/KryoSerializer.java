package io.bitchat.protocol.serialize;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author houyi
 */
@Slf4j
public class KryoSerializer implements Serializer {

    private KryoSerializer() {

    }

    public static Serializer getInstance() {
        return Singleton.get(KryoSerializer.class);
    }

    @Override
    public byte[] serialize(Object object) {
        Assert.notNull(object, "the serialize object can not be null");
        byte[] bytes = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Kryo kryo = PoolHolder.kryoPool.obtain();
        Output output = PoolHolder.outputPool.obtain();
        try {
            output.setOutputStream(outputStream);
            kryo.writeClassAndObject(output, object);
            output.flush();
            bytes = outputStream.toByteArray();
        } catch (Exception e) {
            log.warn("KryoSerializer [serialize] error, cause:{}", e.getMessage(), e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PoolHolder.kryoPool.free(kryo);
            PoolHolder.outputPool.free(output);
        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Assert.notNull(bytes, "the deserialize bytes can not be null");
        T object = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Kryo kryo = PoolHolder.kryoPool.obtain();
        Input input = PoolHolder.inputPool.obtain();
        try {
            input.setBuffer(bytes);
            object = (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            log.warn("KryoSerializer [deserialize] error, cause:{}", e.getMessage(), e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PoolHolder.kryoPool.free(kryo);
            PoolHolder.inputPool.free(input);
        }
        return object;
    }

    /**
     * Hold a Kryo|Output|Input pool
     */
    private static class PoolHolder {

        private static Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8) {
            @Override
            protected Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setRegistrationRequired(false);
                kryo.setReferences(true);
                return kryo;
            }
        };

        private static Pool<Output> outputPool = new Pool<Output>(true, false, 16) {
            @Override
            protected Output create() {
                return new Output(1024, -1);
            }
        };

        private static Pool<Input> inputPool = new Pool<Input>(true, false, 16) {
            @Override
            protected Input create() {
                return new Input(1024);
            }
        };
    }

}
