package io.bitchat.protocol.packet;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.text.StrFormatter;
import io.bitchat.core.executor.AbstractExecutor;
import io.bitchat.protocol.PacketRecognizer;
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
        Packet response;
        ChannelHandlerContext ctx = (ChannelHandlerContext) request[0];
        Packet packet = (Packet) request[1];
        try {
            int symbol = packet.getSymbol();
            PacketHandler handler = recognizer.packetHandler(symbol);
            // if no handler is found
            if (handler == null) {
                log.warn("No handler found with packet={}", packet);
                return null;
            }
            response = handler.handle(ctx, packet);
        } catch (Exception e) {
            response = CarrierPacket.getStringCarrierPacket(false, StrFormatter.format("Server Error,cause={}", e.getMessage()), null);
            response.setId(packet.getId());
        }
        return response;
    }


}
