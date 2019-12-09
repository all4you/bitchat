package io.bitchat.client;

import io.bitchat.core.IdleStateChecker;
import io.bitchat.protocol.PacketCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * @author houyi
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private Client client;

    public ClientInitializer(Client client) {
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateChecker(30));
        pipeline.addLast(new PacketCodec(null));
        pipeline.addLast(new HealthyChecker(client, 15));
        pipeline.addLast(new ClientHandler());
    }

}