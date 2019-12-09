package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import io.bitchat.protocol.Packet;
import io.bitchat.protocol.PacketCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author houyi
 */
public class ProtocolDispatcher extends ChannelInboundHandlerAdapter {

    private ChannelPipeline pipeline;

    private ChannelListener channelListener;

    public ProtocolDispatcher(ChannelPipeline pipeline, ChannelListener channelListener) {
        Assert.notNull(pipeline, "pipeline can not be null");
        Assert.notNull(channelListener, "channelListener can not be null");
        this.pipeline = pipeline;
        this.channelListener = channelListener;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf in = (ByteBuf) msg;
            if(isPacket(in)) {
                pipeline.addLast(new PacketCodec(null));
                pipeline.addLast(PacketHandler.getInstance(channelListener));
            } else {
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new ChunkedWriteHandler());
                // aggregate HttpRequest/HttpContent/LastHttpContent to FullHttpRequest
                pipeline.addLast(new HttpObjectAggregator(8096));
                // handle WebSocketFrame if http/s upgrade to websocket
                pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
            }
        }
        // trigger next channel handler
        ctx.fireChannelRead(msg);
    }

    private boolean isPacket(ByteBuf in) {
        byte magic = in.getByte(0);
        return magic == Packet.PACKET_MAGIC;
    }


}
