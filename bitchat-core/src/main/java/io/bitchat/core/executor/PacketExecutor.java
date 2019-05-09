package io.bitchat.core.executor;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.protocol.PacketRecognizer;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class PacketExecutor extends AbstractExecutor<Packet> {

    private PacketRecognizer recognizer;

    private PacketExecutor(PacketRecognizer recognizer) {
        Assert.notNull(recognizer, "recognizer can not be null");
        this.recognizer = recognizer;
    }

    public static PacketExecutor getInstance(PacketRecognizer recognizer) {
        return Singleton.get(PacketExecutor.class, recognizer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet doExecute(Object... request) {
        ChannelHandlerContext ctx = (ChannelHandlerContext) request[0];
        Packet packet = (Packet) request[1];
        int symbol = packet.getSymbol();
        PacketHandler handler = recognizer.packetHandler(symbol);
        // if no handler is found
        if (handler == null) {
            log.warn("No handler found with packet={}", packet);
            return null;
        }
        return handler.handle(ctx, packet);
    }


}
