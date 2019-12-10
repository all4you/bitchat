package io.bitchat.serialize;

import io.bitchat.packet.Packet;
import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.packet.Request;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author houyi
 */
@Slf4j
public class SerializeTest {

    private Serializer serializer;

    @Before
    public void before() {
        serializer = FastJsonSerializer.getInstance();
    }

    @Test
    public void testDeserialize() {
        Request request = new Request();
        request.setServiceName("io.bitchat.core.EchoService");
        Packet requestPacket = PacketFactory.newRequestPacket(request, 123);
        requestPacket.setHandleAsync(false);
        byte[] content = serializer.serialize(requestPacket);
        Packet deserializePacket = serializer.deserialize(content, requestPacket.getClass());
        log.info("deserializePacket={}", deserializePacket);
        log.info("algorithm={}", deserializePacket.getAlgorithm());
        log.info("handleAsync={}", deserializePacket.isHandleAsync());
    }

}
