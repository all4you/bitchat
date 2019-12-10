package io.bitchat.server.packet;

import cn.hutool.core.lang.Singleton;
import io.bitchat.core.executor.AbstractExecutor;
import io.bitchat.packet.Packet;
import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.packet.Payload;
import io.bitchat.packet.handler.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class PacketExecutor extends AbstractExecutor<Packet> {

    private RequestHandler requestHandler;

    private PacketExecutor() {
        this.requestHandler = RequestHandler.getInstance();
    }

    public static PacketExecutor getInstance() {
        return Singleton.get(PacketExecutor.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Packet doExecute(Object... request) {
        ChannelHandlerContext ctx = (ChannelHandlerContext) request[0];
        Packet packet = (Packet) request[1];
        Payload payload = requestHandler.handle(ctx, packet.getRequest());
        return PacketFactory.newResponsePacket(payload, packet.getId());
    }


}
