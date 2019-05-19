package io.bitchat.client;

import io.bitchat.protocol.IdleStateChecker;
import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.SerializerChooser;
import io.bitchat.protocol.packet.PacketCodec;
import io.bitchat.protocol.DefaultPacketRecognizer;
import io.bitchat.protocol.DefaultSerializerChooser;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * @author houyi
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private Client client;

    /**
     * the serializer chooser
     */
    private SerializerChooser chooser;

    /**
     * the packet recognizer
     */
    private PacketRecognizer recognizer;

    public ClientInitializer(Client client, SerializerChooser chooser, PacketRecognizer recognizer) {
        this.client = client;
        this.chooser = chooser == null ? DefaultSerializerChooser.getInstance() : chooser;
        this.recognizer = recognizer == null ? DefaultPacketRecognizer.getInstance() : recognizer;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateChecker(15));
        pipeline.addLast(new PacketCodec(chooser, recognizer));
        pipeline.addLast(new HealthyChecker(client, 5));
        pipeline.addLast(new ClientPacketDispatcher(recognizer));
    }

}