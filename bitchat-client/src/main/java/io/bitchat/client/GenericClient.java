package io.bitchat.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import io.bitchat.core.client.Client;
import io.bitchat.core.init.Initializer;
import io.bitchat.core.protocol.PacketRecognizer;
import io.bitchat.core.protocol.SerializerChooser;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.core.protocol.packet.PacketCodec;
import io.bitchat.core.router.LoadBalancer;
import io.bitchat.core.server.ServerAttr;
import io.bitchat.protocol.DefaultPacketRecognizer;
import io.bitchat.protocol.DefaultSerializerChooser;
import io.bitchat.protocol.packet.CarrierPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author houyi
 */
@Slf4j
public class GenericClient implements Client {

    private ServerAttr serverAttr;

    private volatile boolean connected = false;

    private Channel channel = null;

    public GenericClient(ServerAttr serverAttr) {
        this.serverAttr = serverAttr;
        Initializer.init();
        connectServer();
    }

    public GenericClient(LoadBalancer loadBalancer) {
        Assert.notNull(loadBalancer, "loadBalancer can not be null");
        this.serverAttr = loadBalancer.nextServer();
        Initializer.init();
        connectServer();
    }

    private void connectServer() {
        Assert.notNull(serverAttr, "serverAttr can not be null");
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ClientInitializer(null, null));
                    }
                });

        ChannelFuture future = bootstrap.connect(serverAttr.getAddress(), serverAttr.getPort());
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> f) throws Exception {
                if (f.isSuccess()) {
                    channel = future.channel();
                    connected = true;
                    log.debug("[{}] has connected to {}", GenericClient.class.getSimpleName(), serverAttr);
                } else {
                    log.error("[{}] connected to {} failed, cause={}", GenericClient.class.getSimpleName(), serverAttr, f.cause().getMessage());
                }
            }
        });
    }

    @Override
    public String id() {
        return IdUtil.objectId();
    }

    @Override
    public CompletableFuture<Packet> sendRequest(Packet request) {
        // create a promise
        CompletableFuture<Packet> promise = new CompletableFuture<>();
        if (!connected) {
            log.debug("Not connected yet!");
            promise.complete(CarrierPacket.getStringCarrierPacket(false, "Not connected yet!", null));
            return promise;
        }
        Long id = request.getId();
        PendingRequests.add(id, promise);
        ChannelFuture future = channel.writeAndFlush(request);
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> f) throws Exception {
                if (!f.isSuccess()) {
                    CompletableFuture<Packet> pending = PendingRequests.remove(id);
                    if (pending != null) {
                        pending.completeExceptionally(f.cause());
                    }
                }
            }
        });
        return promise;
    }


    private class ClientInitializer extends ChannelInitializer<SocketChannel> {

        /**
         * the serializer chooser
         */
        private SerializerChooser chooser;

        /**
         * the packet recognizer
         */
        private PacketRecognizer recognizer;

        ClientInitializer(SerializerChooser chooser, PacketRecognizer recognizer) {
            this.chooser = chooser == null ? DefaultSerializerChooser.getInstance() : chooser;
            this.recognizer = recognizer == null ? DefaultPacketRecognizer.getInstance() : recognizer;
        }

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new PacketCodec(chooser, recognizer));
            pipeline.addLast(new ClientPacketDispatcher(recognizer));
        }
    }

}
