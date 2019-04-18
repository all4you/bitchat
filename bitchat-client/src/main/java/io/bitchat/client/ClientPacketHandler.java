package io.bitchat.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.protocol.PacketRecognizer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houyi
 */
@Slf4j
public class ClientPacketHandler extends SimpleChannelInboundHandler<Packet> {

    private PacketRecognizer recognizer;
    private AbstractClient abstractClient;

    public ClientPacketHandler(PacketRecognizer recognizer, AbstractClient abstractClient) {
        Assert.notNull(recognizer, "recognizer can not be null");
        Assert.notNull(abstractClient, "abstractClient can not be null");
        this.recognizer = recognizer;
        this.abstractClient = abstractClient;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet response) {
        int symbol = response.getSymbol();
        Class<? extends PacketHandler> handlerClass = recognizer.packetHandler(symbol);
        if (handlerClass != null) {
            PacketHandler handler = Singleton.get(handlerClass);
            handler.handle(null, response);
        } else {
            abstractClient.receiveResponse(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error("ctx close,cause:", cause);
    }

}
