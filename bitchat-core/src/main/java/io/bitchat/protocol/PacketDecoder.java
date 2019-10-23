package io.bitchat.protocol;

import io.bitchat.serialize.DefaultSerializerChooser;
import io.bitchat.serialize.Serializer;
import io.bitchat.serialize.SerializerChooser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * <p>
 * the header field contains:
 * <ul>
 * <li>magic---1byte</li>
 * <li>algorithm---1byte</li>
 * <li>type---1byte</li>
 * </ul>
 * so the length field offset is 3
 * the length field is 4 bytes
 * </p>
 * <p>
 * see {@link Packet} for detail
 *
 * @author houyi
 */
public class PacketDecoder extends LengthFieldBasedFrameDecoder {

    private SerializerChooser chooser;

    public final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;

    public final int LENGTH_FIELD_OFFSET = 3;

    public final int LENGTH_FIELD_LENGTH = 4;

    public PacketDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.chooser = DefaultSerializerChooser.getInstance();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // get the whole tcp package
        // decoded by super class
        in = (ByteBuf) super.decode(ctx, in);

        // check the header length
        int leastPacketLen = LENGTH_FIELD_OFFSET + LENGTH_FIELD_LENGTH;
        if (in.readableBytes() < leastPacketLen) {
            throw new DecoderException("packet header length less than leastPacketLen=" + leastPacketLen);
        }

        byte magic = in.readByte();
        byte algorithm = in.readByte();
        Serializer serializer = chooser.choose(algorithm);
        if (serializer == null) {
            throw new DecoderException("serialize algorithm is invalid");
        }

        byte type = in.readByte();
        int len = in.readInt();
        if (in.readableBytes() != len) {
            throw new DecoderException("packet content marked length is not equals to actual length");
        }

        // read content
        byte[] content = new byte[len];
        in.readBytes(content);

        return serializer.deserialize(content, DefaultPacket.class);
    }

}
