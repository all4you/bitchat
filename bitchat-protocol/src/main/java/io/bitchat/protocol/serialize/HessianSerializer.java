package io.bitchat.protocol.serialize;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author houyi
 */
@Slf4j
public class HessianSerializer implements Serializer {

    private HessianSerializer() {

    }

    public static Serializer getInstance() {
        return Singleton.get(HessianSerializer.class);
    }

    @Override
    public byte[] serialize(Object object) {
        Assert.notNull(object, "the serialize object can not be null");
        byte[] bytes = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        try {
            hessian2Output.writeObject(object);
            hessian2Output.flush();
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            log.warn("HessianSerializer [serialize] error, cause:{}", e.getMessage(), e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Assert.notNull(bytes, "the deserialize bytes can not be null");
        T object = null;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Hessian2Input hessian2Input = new Hessian2Input(inputStream);
        try {
            object = (T) hessian2Input.readObject();
        } catch (IOException e) {
            log.warn("HessianSerializer [deserialize] error, cause:{}", e.getMessage(), e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

}
