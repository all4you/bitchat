package io.bitchat.protocol.serialize;

import io.bitchat.core.lang.enums.SerializeAlgorithm;
import io.bitchat.core.message.GroupMessage;
import io.bitchat.core.protocol.SerializerChooser;
import io.bitchat.core.protocol.serialize.Serializer;
import io.bitchat.protocol.DefaultSerializerChooser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;


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
        GroupMessage object = GroupMessage.builder().groupId(1L).userId(2L).build();
        log.info("object={}", object);
        Serializer serializer = chooser.choose(SerializeAlgorithm.HESSIAN.getType());
        doSerialize(serializer, object);
    }

    @Test
    public void testAllSerialize() {
        GroupMessage object = GroupMessage.builder().groupId(1L).userId(2L).build();
        log.info("object={}", object);
        for (SerializeAlgorithm algorithm : SerializeAlgorithm.values()) {
            Serializer serializer = chooser.choose(algorithm.getType());
            doSerialize(serializer, object);
        }
    }

    private void doSerialize(Serializer serializer, GroupMessage object) {
        long start = System.currentTimeMillis();
        GroupMessage newObj = null;
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


}
