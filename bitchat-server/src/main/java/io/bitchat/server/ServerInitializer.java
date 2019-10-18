package io.bitchat.server;

import io.bitchat.core.IdleStateChecker;
import io.bitchat.protocol.PacketCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author houyi
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * the channel listener
     */
    private ChannelListener channelListener;

    public ServerInitializer(ChannelListener channelListener) {
        this.channelListener = channelListener;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateChecker(15));
        pipeline.addLast(new PacketCodec());
        pipeline.addLast(ServerHandler.getInstance(channelListener));
    }

}
