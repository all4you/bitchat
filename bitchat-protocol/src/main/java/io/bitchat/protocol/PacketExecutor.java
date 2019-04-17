package io.bitchat.protocol;

import cn.hutool.core.lang.Singleton;
import io.bitchat.core.executor.AbstractExecutor;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.protocol.packet.PacketRecognizer;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class PacketExecutor extends AbstractExecutor<Packet> {

    private PacketRecognizer recognizer;

    private PacketExecutor(PacketRecognizer recognizer) {
        this.recognizer = recognizer == null ? DefaultPacketRecognizer.getInstance() : recognizer;
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
        Class<? extends PacketHandler> handlerClass = recognizer.packetHandler(symbol);
        // if no handler is found
        if (handlerClass == null) {
            log.warn("No handler found with packet={}", packet);
            return null;
        }
        PacketHandler handler = Singleton.get(handlerClass);
        return handler.handle(ctx, packet);
    }


}
