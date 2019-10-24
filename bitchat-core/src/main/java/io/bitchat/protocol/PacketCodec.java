package io.bitchat.protocol;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.bitchat.serialize.DefaultSerializerChooser;
import io.bitchat.serialize.SerializeAlgorithm;
import io.bitchat.serialize.Serializer;
import io.bitchat.serialize.SerializerChooser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 * A Generic Packet Codec
 * </p>
 *
 * @author houyi
 */
@Slf4j
public class PacketCodec extends ByteToMessageCodec<Packet> {

    private SerializerChooser chooser;

    public PacketCodec(SerializerChooser chooser) {
        this.chooser = chooser != null ? chooser : DefaultSerializerChooser.getInstance();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf in) throws Exception {
        // check the packet
        if (!checkPacket(packet)) {
            throw new RuntimeException("checkPacket failed!");
        }
        byte algorithm = packet.getAlgorithm();
        Serializer serializer = chooser.choose(algorithm);
        // get packet content bytes
        byte[] content = serializer.serialize(packet);
        // do encode
        in.writeByte(packet.getMagic());
        in.writeByte(algorithm);
        in.writeByte(packet.getType());
        in.writeInt(content.length);
        in.writeBytes(content);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // leastPacketLen = 1byte(magic) + 1byte(serialize algorithm) + 1byte(packet type) + 4bytes(content length)
        int leastPacketLen = 7;
        // until we can read at least 7 bytes
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
        Assert.state(magic == Packet.PACKET_MAGIC, "magic number is invalid");

        byte algorithm = in.readByte();
        Serializer serializer = chooser.choose(algorithm);
        Assert.notNull(serializer, "No serializer is chosen cause the algorithm of packet is invalid");
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

        Packet packet = serializer.deserialize(content, DefaultPacket.class);
        out.add(packet);
    }

    private boolean checkPacket(Packet packet) {
        byte algorithm = packet.getAlgorithm();
        SerializeAlgorithm serializeAlgorithm = SerializeAlgorithm.getEnum(algorithm);
        if (serializeAlgorithm == null) {
            log.error("algorithm={} is invalid with packet={}", algorithm, packet);
            return false;
        }
        byte type = packet.getType();
        if (type != PacketType.PACKET_TYPE_REQUEST && type != PacketType.PACKET_TYPE_RESPONSE && type != PacketType.PACKET_TYPE_COMMAND) {
            log.error("packet type={} is invalid with packet={}", type, packet);
            return false;
        }
        long id = packet.getId();
        if (type != PacketType.PACKET_TYPE_COMMAND && id == 0) {
            log.error("id=0 is invalid with packet={}", packet);
            return false;
        }
        Request request = packet.getRequest();
        if (type == PacketType.PACKET_TYPE_REQUEST) {
            if (request == null) {
                log.error("packet request can not be null with packet={}", packet);
                return false;
            }
            String serviceName = request.getServiceName();
            if (StrUtil.isBlank(serviceName)) {
                log.error("serviceName can not be blank with packet={}", packet);
                return false;
            }
        }
        Command command = packet.getCommand();
        if (type == PacketType.PACKET_TYPE_COMMAND) {
            if (command == null) {
                log.error("packet command can not be null with packet={}", packet);
                return false;
            }
            String commandName = command.getCommandName();
            if (StrUtil.isBlank(commandName)) {
                log.error("commandName can not be blank with packet={}", packet);
                return false;
            }
        }
        return true;
    }

}
