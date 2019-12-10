package io.bitchat.server;

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
        pipeline.addLast(new ProtocolDispatcher(channelListener));
    }

}
