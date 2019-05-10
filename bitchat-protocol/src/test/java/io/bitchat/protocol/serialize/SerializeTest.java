package io.bitchat.protocol.serialize;

import io.bitchat.protocol.SerializerChooser;
import io.bitchat.protocol.DefaultSerializerChooser;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;


/**
 * @author houyi
 */
@Slf4j
public class SerializeTest {

    private SerializerChooser chooser;

    @Before
    public void initChooser() {
        chooser = DefaultSerializerChooser.getInstance();
    }

    @Test
    public void testSingleSerialize() {
        SerializableGroup object = SerializableGroup.builder().groupId(1L).userId(2L).build();
        log.info("object={}", object);
        Serializer serializer = chooser.choose(SerializeAlgorithm.HESSIAN.getType());
        doSerialize(serializer, object);
    }

    @Test
    public void testAllSerialize() {
        SerializableGroup object = SerializableGroup.builder().groupId(1L).userId(2L).build();
        log.info("object={}", object);
        for (SerializeAlgorithm algorithm : SerializeAlgorithm.values()) {
            Serializer serializer = chooser.choose(algorithm.getType());
            doSerialize(serializer, object);
        }
    }

    private void doSerialize(Serializer serializer, SerializableGroup object) {
        long start = System.currentTimeMillis();
        SerializableGroup newObj = null;
        byte[] bytes = null;
        int loop = 10000;
        int i = 0;
        while (i++ < loop) {
            bytes = serializer.serialize(object);
            newObj = serializer.deserialize(bytes, object.getClass());
        }
        long end = System.currentTimeMillis();
        log.info("cost={}[ms], serializer={}, bytes size={}, newObj={}", (end - start), serializer, bytes == null ? 0 : bytes.length, newObj);
    }

    @Data
    @Builder
    public static class SerializableGroup implements Serializable {
        private Long groupId;
        private Long userId;
    }

}
