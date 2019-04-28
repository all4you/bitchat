package io.bitchat.server;

import io.bitchat.core.protocol.packet.PacketCodec;
import io.bitchat.core.protocol.PacketRecognizer;
import io.bitchat.core.protocol.SerializerChooser;
import io.bitchat.protocol.DefaultPacketRecognizer;
import io.bitchat.protocol.DefaultSerializerChooser;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author houyi
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * the serializer chooser
     */
    private SerializerChooser chooser;

    /**
     * the packet recognizer
     */
    private PacketRecognizer recognizer;

    public ServerInitializer(SerializerChooser chooser, PacketRecognizer recognizer) {
        this.chooser = chooser == null ? DefaultSerializerChooser.getInstance() : chooser;
        this.recognizer = recognizer == null ? DefaultPacketRecognizer.getInstance() : recognizer;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new PacketCodec(chooser, recognizer));
        pipeline.addLast(ServerPacketDispatcher.getInstance(recognizer));
    }

}
