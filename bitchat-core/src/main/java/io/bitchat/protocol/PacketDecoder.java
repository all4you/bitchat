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

    public static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;

    public static final int LENGTH_FIELD_OFFSET = 3;

    public static final int LENGTH_FIELD_LENGTH = 4;

    public PacketDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.chooser = DefaultSerializerChooser.getInstance();
    }

    @Override
    protected Packet decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // get the whole tcp package
        // decoded by super class
        ByteBuf decodedIn = (ByteBuf) super.decode(ctx, in);
        if (decodedIn == null) {
            return null;
        }

        // check the header length
        int leastPacketLen = LENGTH_FIELD_OFFSET + LENGTH_FIELD_LENGTH;
        if (decodedIn.readableBytes() < leastPacketLen) {
            throw new DecoderException("packet header length less than leastPacketLen=" + leastPacketLen);
        }

        byte magic = decodedIn.readByte();
        byte algorithm = decodedIn.readByte();
        Serializer serializer = chooser.choose(algorithm);
        if (serializer == null) {
            throw new DecoderException("serialize algorithm is invalid");
        }

        byte type = decodedIn.readByte();
        int len = decodedIn.readInt();
        if (decodedIn.readableBytes() != len) {
            throw new DecoderException("packet content marked length is not equals to actual length");
        }

        // read content
        byte[] content = new byte[len];
        decodedIn.readBytes(content);

        return serializer.deserialize(content, DefaultPacket.class);
    }

}
