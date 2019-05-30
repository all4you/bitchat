package io.bitchat.server;

import cn.hutool.core.util.IdUtil;
import io.bitchat.core.ServerAttr;
import io.bitchat.core.lang.config.BaseConfig;
import io.bitchat.core.lang.config.ConfigFactory;
import io.bitchat.core.init.Initializer;
import io.bitchat.protocol.DefaultPacketRecognizer;
import io.bitchat.protocol.DefaultSerializerChooser;
import io.bitchat.protocol.PacketRecognizer;
import io.bitchat.protocol.SerializerChooser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * A abstract server
 * </p>
 *
 * @author houyi
 */
@Slf4j
public abstract class AbstractServer implements Server {

    /**
     * the server attribute
     */
    private ServerAttr serverAttr;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private SerializerChooser chooser;

    private PacketRecognizer recognizer;

    private ChannelListener channelListener;

    /**
     * whether the server is started
     */
    private AtomicBoolean started = new AtomicBoolean(false);

    public AbstractServer(Integer serverPort) {
        this(serverPort, null, null, null);
    }

    public AbstractServer(Integer serverPort, SerializerChooser chooser, PacketRecognizer recognizer, ChannelListener channelListener) {
        int port = serverPort == null ? ConfigFactory.getConfig(BaseConfig.class).serverPort() : serverPort;
        this.serverAttr = ServerAttr.getLocalServer(port);
        this.chooser = chooser == null ? DefaultSerializerChooser.getInstance() : chooser;
        this.recognizer = recognizer == null ? DefaultPacketRecognizer.getInstance() : recognizer;
        this.channelListener = channelListener == null ? DefaultChannelListener.getInstance() : channelListener;
    }

    @Override
    public ServerAttr attribute() {
        return serverAttr;
    }

    @Override
    public void start() {
        // do init work before server start
        Initializer.init();
        if (started.compareAndSet(false, true)) {
            doStart(serverAttr.getPort());
        }
    }

    @Override
    public void stop() {
        if (started.get()) {
            log.warn("Server hasn't started yet!");
            return;
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public String id() {
        return IdUtil.objectId();
    }

    public void doStart(int port) {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        long start = System.currentTimeMillis();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer(chooser, recognizer, channelListener));

        ChannelFuture future = bootstrap.bind(port);
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    long cost = System.currentTimeMillis() - start;
                    log.info("[{}] Startup at port:{} cost:{}[ms]", AbstractServer.class.getSimpleName(), port, cost);
                    // register to router after startup successfully
                    registerToRouter();
                }
            }
        });
    }


}
