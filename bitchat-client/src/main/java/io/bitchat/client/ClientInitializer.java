package io.bitchat.client;

import io.bitchat.core.IdleStateChecker;
import io.bitchat.core.protocol.PacketRecognizer;
import io.bitchat.core.serialize.SerializerChooser;
import io.bitchat.core.protocol.PacketCodec;
import io.bitchat.core.protocol.DefaultPacketRecognizer;
import io.bitchat.core.serialize.DefaultSerializerChooser;
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