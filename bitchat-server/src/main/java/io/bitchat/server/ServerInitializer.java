package io.bitchat.server;

import io.bitchat.core.IdleStateChecker;
import io.bitchat.core.packet.PacketRecognizer;
import io.bitchat.core.serialize.SerializerChooser;
import io.bitchat.core.packet.PacketCodec;
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

    /**
     * the channel listener
     */
    private ChannelListener channelListener;

    public ServerInitializer(SerializerChooser chooser, PacketRecognizer recognizer, ChannelListener channelListener) {
        this.chooser = chooser;
        this.recognizer = recognizer;
        this.channelListener = channelListener;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateChecker(15));
        pipeline.addLast(new PacketCodec(chooser, recognizer));
        pipeline.addLast(ServerPacketDispatcher.getInstance(recognizer, channelListener));
    }

}
