package io.bitchat.ws.codec;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import io.bitchat.serialize.ProtoStuffSerializer;
import io.bitchat.serialize.Serializer;
import io.bitchat.ws.Frame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 * A Generic Frame Codec
 * </p>
 *
 * @author houyi
 */
@Slf4j
@ChannelHandler.Sharable
public class FrameCodec extends MessageToMessageCodec<WebSocketFrame, Frame> {

    private static Serializer serializer = ProtoStuffSerializer.getInstance();

    private FrameCodec() {

    }

    public static FrameCodec getInstance() {
        return Singleton.get(FrameCodec.class);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Frame frame, List<Object> out) throws Exception {
        ByteBuf in = ByteBufAllocator.DEFAULT.buffer();
        // check the frame
        if (!checkFrame(frame)) {
            throw new RuntimeException("checkFrame failed!");
        }
        // get frame content bytes
        byte[] content = serializer.serialize(frame);
        // do encode
        in.writeByte(frame.getMagic());
        in.writeByte(frame.getType());
        in.writeInt(content.length);
        in.writeBytes(content);

        out.add(new BinaryWebSocketFrame(in));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf in = msg.content();
        // leastPacketLen = 1byte(magic) + 1byte(packet type) + 4bytes(content length)
        int leastPacketLen = 6;
        // until we can read at least 6 bytes
        if (in.readableBytes() < leastPacketLen) {
            return;
        }
        // mark reader index at here
        // if no enough bytes arrived
        // we should wait and reset the
        // reader index to here
        in.markReaderIndex();
        // do common check before decode
        byte magic = in.readByte();
        Assert.state(magic == Frame.FRAME_MAGIC, "magic number is invalid");
        byte type = in.readByte();
        int len = in.readInt();
        // until we have the entire packet received
        if (in.readableBytes() < len) {
            // after read some bytes: magic/algorithm/type/len
            // the left readable bytes length is less than len
            // so we need to wait until enough bytes received
            // but we must reset the reader index to we marked
            // before we return
            in.resetReaderIndex();
            return;
        }
        // read content
        byte[] content = new byte[len];
        in.readBytes(content);

        Frame frame = serializer.deserialize(content, Frame.class);
        out.add(frame);

    }

    private boolean checkFrame(Frame frame) {
        byte magic = frame.getMagic();
        if (magic != Frame.FRAME_MAGIC) {
            log.error("magic={} is invalid with frame={}", magic, frame);
            return false;
        }
        return true;
    }

}
