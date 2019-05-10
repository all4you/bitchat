package io.bitchat.protocol.packet;

import io.bitchat.core.init.Initializer;
import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.DefaultPacketRecognizer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author houyi
 */
@Slf4j
public class PacketRecognizerTest {

    private PacketRecognizer recognizer;

    @Before
    public void init() {
        Initializer.init();
        recognizer = DefaultPacketRecognizer.getInstance();
    }

    @Test
    public void testRecognizer() {
        int symbol = -1;
        log.info("packet={}",recognizer.packet(symbol));
        log.info("handler={}",recognizer.packetHandler(symbol));
    }

}
