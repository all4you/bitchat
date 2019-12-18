package io.bitchat.client;

import cn.hutool.core.lang.Assert;
import io.bitchat.core.LoadBalancer;
import io.bitchat.packet.PendingPackets;
import io.bitchat.core.ServerAttr;
import io.bitchat.core.init.Initializer;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.packet.Packet;
import io.bitchat.packet.factory.PacketFactory;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.PayloadFactory;
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
    }

    public GenericClient(LoadBalancer loadBalancer) {
        Assert.notNull(loadBalancer, "loadBalancer can not be null");
        this.serverAttr = loadBalancer.nextServer();
        Initializer.init();
    }

    @Override
    public void connect() {
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
                        pipeline.addLast(new ClientInitializer(GenericClient.this));
                    }
                });

        ChannelFuture future = bootstrap.connect(serverAttr.getAddress(), serverAttr.getPort());
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> f) throws Exception {
                channel = future.channel();
                if (f.isSuccess()) {
                    connected = true;
                    log.info("[{}] Has connected to {} successfully", GenericClient.class.getSimpleName(), serverAttr);
                } else {
                    log.warn("[{}] Connect to {} failed, cause={}", GenericClient.class.getSimpleName(), serverAttr, f.cause().getMessage());
                    // fire the channelInactive and make sure
                    // the {@link HealthyChecker} will reconnect
                    channel.pipeline().fireChannelInactive();
                }
            }
        });
    }

    @Override
    public CompletableFuture<Packet> sendRequest(Packet request) {
        // create a promise
        CompletableFuture<Packet> promise = new CompletableFuture<>();
        if (!connected) {
            String msg = "Not connected yet!";
            log.debug(msg);
            Payload payload = PayloadFactory.newErrorPayload(ResultCode.BIZ_FAIL.getCode(), msg);
            promise.complete(PacketFactory.newResponsePacket(payload, request.getId()));
            return promise;
        }
        Long id = request.getId();
        PendingPackets.add(id, promise);
        ChannelFuture future = channel.writeAndFlush(request);
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> f) throws Exception {
                if (!f.isSuccess()) {
                    CompletableFuture<Packet> pending = PendingPackets.remove(id);
                    if (pending != null) {
                        pending.completeExceptionally(f.cause());
                    }
                }
            }
        });
        return promise;
    }


}
