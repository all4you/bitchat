package io.bitchat.server;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.IdleStateChecker;
import io.bitchat.lang.config.BaseConfig;
import io.bitchat.lang.config.ConfigFactory;
import io.bitchat.protocol.Packet;
import io.bitchat.protocol.PacketCodec;
import io.bitchat.server.http.HttpHandler;
import io.bitchat.server.packet.PacketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.List;

/**
 * dispatch different protocols
 * <p>
 * see {@link io.netty.example.portunification.PortUnificationServerHandler}
 *
 * @author houyi
 */
public class ProtocolDispatcher extends ByteToMessageDecoder {

    private ChannelListener channelListener;

    public ProtocolDispatcher(ChannelListener channelListener) {
        Assert.notNull(channelListener, "channelListener can not be null");
        this.channelListener = channelListener;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // Will use the first five bytes to detect a protocol.
        if (in.readableBytes() < 5) {
            return;
        }

        final int magic1 = in.getByte(in.readerIndex());
        final int magic2 = in.getByte(in.readerIndex() + 1);
        if (isPacket(magic1)) {
            dispatchToPacket(ctx);
        } else if (isHttp(magic1, magic2)) {
            dispatchToHttp(ctx);
        } else {
            // Unknown protocol; discard everything and close the connection.
            in.clear();
            ctx.close();
        }
    }

    private boolean isPacket(int magic1) {
        return magic1 == Packet.PACKET_MAGIC;
    }

    private static boolean isHttp(int magic1, int magic2) {
        return
                magic1 == 'G' && magic2 == 'E' || // GET
                magic1 == 'P' && magic2 == 'O' || // POST
                magic1 == 'P' && magic2 == 'U' || // PUT
                magic1 == 'H' && magic2 == 'E' || // HEAD
                magic1 == 'O' && magic2 == 'P' || // OPTIONS
                magic1 == 'P' && magic2 == 'A' || // PATCH
                magic1 == 'D' && magic2 == 'E' || // DELETE
                magic1 == 'T' && magic2 == 'R' || // TRACE
                magic1 == 'C' && magic2 == 'O';   // CONNECT
    }

    private void dispatchToPacket(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        BaseConfig baseConfig = ConfigFactory.getConfig(BaseConfig.class);
        pipeline.addLast(new IdleStateChecker(baseConfig.readerIdleTime()));
        pipeline.addLast(new PacketCodec());
        pipeline.addLast(PacketHandler.getInstance(channelListener));
        // 将所有所需的ChannelHandler添加到pipeline之后，一定要将自身移除掉
        // 否则该Channel之后的请求仍会重新执行协议的分发，而这是要避免的
        pipeline.remove(this);
    }

    private void dispatchToHttp(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        // aggregate HttpRequest/HttpContent/LastHttpContent to FullHttpRequest
        pipeline.addLast(new HttpObjectAggregator(8096));
        pipeline.addLast(HttpHandler.getInstance());
        // handle WebSocketFrame if http/s upgrade to websocket
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.remove(this);
    }

}
