package io.bitchat.client;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import io.bitchat.core.Listener;
import io.bitchat.core.Carrier;
import io.bitchat.core.client.Client;
import io.bitchat.core.lang.enums.MessageType;
import io.bitchat.core.lang.id.IdFactory;
import io.bitchat.core.lang.init.Initializer;
import io.bitchat.core.lang.id.StandaloneMemoryIdFactory;
import io.bitchat.core.message.Message;
import io.bitchat.core.protocol.packet.Packet;
import io.bitchat.core.protocol.packet.PacketCodec;
import io.bitchat.core.protocol.packet.PacketHandler;
import io.bitchat.core.protocol.packet.PacketRecognizer;
import io.bitchat.core.protocol.serialize.SerializerChooser;
import io.bitchat.core.router.LoadBalancer;
import io.bitchat.core.server.ServerAttr;
import io.bitchat.protocol.DefaultPacketRecognizer;
import io.bitchat.protocol.packet.LoginRequestPacket;
import io.bitchat.protocol.packet.MsgCarrierPacket;
import io.bitchat.protocol.packet.P2pMsgRequestPacket;
import io.bitchat.protocol.serialize.DefaultSerializerChooser;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author houyi
 */
@Slf4j
public abstract class AbstractClient implements Client {

    private ServerAttr serverAttr;

    /**
     * the serializer chooser
     */
    private SerializerChooser chooser;

    /**
     * the packet recognizer
     */
    private PacketRecognizer recognizer;

    private IdFactory idFactory = StandaloneMemoryIdFactory.getInstance();

    private volatile boolean connected = false;

    private volatile boolean login = false;

    private Channel channel = null;

    public AbstractClient(ServerAttr serverAttr) {
        this.serverAttr = serverAttr;
        Initializer.init();
    }

    public AbstractClient(LoadBalancer loadBalancer) {
        this.serverAttr = loadBalancer.nextServer();
        Initializer.init();
    }

    public AbstractClient chooser(SerializerChooser chooser) {
        this.chooser = chooser;
        return this;
    }

    public AbstractClient recognizer(PacketRecognizer recognizer) {
        this.recognizer = recognizer;
        return this;
    }

    public AbstractClient idFactory(IdFactory idFactory) {
        if (idFactory != null) {
            this.idFactory = idFactory;
        }
        return this;
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
                        pipeline.addLast(new ClientInitializer(chooser, recognizer));
                    }
                });

        ChannelFuture future = bootstrap.connect(serverAttr.getAddress(), serverAttr.getPort());
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> f) throws Exception {
                if (f.isSuccess()) {
                    channel = future.channel();
                    connected = true;
                    log.debug("[{}] has connected to {}", AbstractClient.class.getSimpleName(), serverAttr);
                } else {
                    log.error("[{}] connected to {} failed,cause={}", AbstractClient.class.getSimpleName(), serverAttr, f.cause().getMessage());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void login(String userName, String password, Listener<Carrier<String>> listener) {
        if (login) {
            log.debug("Already logged in");
            Carrier<String> carrier = Carrier.<String>builder().success(false).errorMsg("Already logged in").build();
            listener.onEvent(carrier);
            return;
        }
        LoginRequestPacket request = LoginRequestPacket.builder()
                .userName(userName)
                .password(password)
                .build();
        request.setId(idFactory.nextId());
        CompletableFuture<Packet> future = sendRequest(request);
        future.whenComplete(new BiConsumer<Packet, Throwable>() {
            @Override
            public void accept(Packet packet, Throwable throwable) {
                Carrier<String> carrier;
                if (throwable != null) {
                    carrier = Carrier.<String>builder().success(false).errorMsg(throwable.getMessage()).build();
                } else {
                    MsgCarrierPacket<String> response = (MsgCarrierPacket) packet;
                    carrier = Carrier.<String>builder().success(response.isSuccess()).errorMsg(response.getMsg()).build();
                    login = response.isSuccess();
                }
                listener.onEvent(carrier);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sendP2pMsg(Long partnerId, MessageType type, String msg, Listener<Carrier<String>> listener) {
        P2pMsgRequestPacket request = P2pMsgRequestPacket.builder()
                .partnerId(partnerId)
                .type(type)
                .msg(msg)
                .build();
        request.setId(idFactory.nextId());
        CompletableFuture<Packet> future = sendRequest(request);
        future.whenComplete(new BiConsumer<Packet, Throwable>() {
            @Override
            public void accept(Packet packet, Throwable throwable) {
                Carrier<String> carrier;
                if (throwable != null) {
                    carrier = Carrier.<String>builder().success(false).errorMsg(throwable.getMessage()).build();
                } else {
                    MsgCarrierPacket<String> response = (MsgCarrierPacket) packet;
                    carrier = Carrier.<String>builder().success(response.isSuccess()).errorMsg(response.getMsg()).build();
                }
                listener.onEvent(carrier);
            }
        });
    }

    @Override
    public void sendGroupMsg(Long groupId, MessageType type, String msg, Listener<Carrier<String>> listener) {

    }

    @Override
    public void fetchP2pHistoryMsg(Long partnerId, Long currentMsgId, boolean fetchForward, Integer fetchSize, Listener<Carrier<List<Message>>> listener) {

    }

    @Override
    public void fetchGroupHistoryMsg(Long partnerId, Long currentMsgId, boolean fetchForward, Integer fetchSize, Listener<Carrier<List<Message>>> listener) {

    }

    @Override
    public void fetchOfflineMsg(Listener<Carrier<List<Message>>> listener) {

    }

    @Override
    public String id() {
        return IdUtil.objectId();
    }

    private CompletableFuture<Packet> sendRequest(Packet request) {
        // create a promise
        CompletableFuture<Packet> promise = new CompletableFuture<>();
        if (!connected) {
            log.debug("Not connected yet!");
            MsgCarrierPacket<String> response = MsgCarrierPacket.<String>builder().success(false).msg("Not connected yet!").build();
            promise.complete(response);
            return promise;
        }
        if (!login && !(request instanceof LoginRequestPacket)) {
            log.debug("Not logged in yet!");
            MsgCarrierPacket<String> response = MsgCarrierPacket.<String>builder().success(false).msg("Not logged in yet!").build();
            promise.complete(response);
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


    /**
     * there are two ways the client will receive the response
     * 1: the result packet which the client request
     * this kind of response will be handled by client itself
     * 2: the message packet which the server pushed initiative
     * this kind of response will be handled by {@link PacketHandler}
     *
     * @param response the response
     */
    public void receiveResponse(Packet response) {
        log.debug("AbstractClient has received {}", response);
        CompletableFuture<Packet> pending = PendingRequests.remove(response.getId());
        if (pending != null) {
            // the response will be handled by client
            // after the client future has been notified
            // to be completed
            pending.complete(response);
        }
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
            pipeline.addLast(new ClientPacketHandler(recognizer, AbstractClient.this));
        }
    }

}
