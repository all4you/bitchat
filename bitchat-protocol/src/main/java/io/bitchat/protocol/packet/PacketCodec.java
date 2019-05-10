package io.bitchat.protocol.packet;

import cn.hutool.core.lang.Assert;
import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.serialize.Serializer;
import io.bitchat.protocol.SerializerChooser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * <p>
 * A Generic Packet Codec
 * </p>
 *
 * @author houyi
 */
public class PacketCodec extends ByteToMessageCodec<Packet> {

    private SerializerChooser chooser;
    private PacketRecognizer recognizer;

    public PacketCodec(SerializerChooser chooser, PacketRecognizer recognizer) {
        Assert.notNull(chooser, "chooser can not be null");
        Assert.notNull(recognizer, "recognizer can not be null");
        this.chooser = chooser;
        this.recognizer = recognizer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf in) throws Exception {
        byte algorithm = packet.algorithm();
        Serializer serializer = chooser.choose(algorithm);
        Assert.notNull(serializer, "No serializer is chosen cause the algorithm of packet is invalid");
        // get packet content bytes
        byte[] content = serializer.serialize(packet);
        // do encode
        in.writeByte(packet.getMagic());
        in.writeByte(algorithm);
        in.writeInt(packet.symbol());
        in.writeInt(content.length);
        in.writeBytes(content);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // do common check before decode
        byte magic = in.readByte();
        Assert.state(magic == Packet.PACKET_MAGIC, "magic number is invalid");
        byte algorithm = in.readByte();
        Serializer serializer = chooser.choose(algorithm);
        Assert.notNull(serializer, "No serializer is chosen cause the algorithm of packet is invalid");
        // leastPacketLen = 1byte(magic) + 1byte(serialize algorithm) + 4bytes(packet type) + 4bytes(content length)
        int leastPacketLen = 10;
        // until we can read at least 10 bytes
        if (in.readableBytes() < leastPacketLen) {
            return;
        }
        int symbol = in.readInt();
        int len = in.readInt();
        // until we have the entire packet received
        if (in.readableBytes() < len) {
            return;
        }
        // read content
        byte[] content = new byte[len];
        in.readBytes(content);

        Class<? extends Packet> packetClass = recognizer.packet(symbol);
        Assert.notNull(packetClass, "No packetClass is recognized cause the symbol={} of packet is invalid", symbol);
        Packet packet = serializer.deserialize(content, packetClass);
        out.add(packet);
    }

}
