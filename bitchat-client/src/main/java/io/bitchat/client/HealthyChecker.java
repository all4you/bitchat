package io.bitchat.client;

import cn.hutool.core.lang.Assert;
import io.bitchat.protocol.packet.PingPacket;
import io.bitchat.protocol.packet.PongPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * This ChannelHandler will check
 * whether the Channel is healthy or not
 * </p>
 *
 * <p>
 * It will keep a heart beat with
 * the Server by send a {@link PingPacket}
 * and the Server will return a {@link PongPacket}
 * </p>
 *
 * <p>
 * If the Channel is InActive it will
 * try to reconnect to Server
 * </p>
 *
 * @author houyi
 */
@Slf4j
public class HealthyChecker extends ChannelInboundHandlerAdapter {

    private static final int DEFAULT_PING_INTERVAL = 5;

    private Client client;

    private int pingInterval;

    public HealthyChecker(Client client, int pingInterval) {
        Assert.notNull(client, "client can not be null");
        this.client = client;
        this.pingInterval = pingInterval <= 0 ? DEFAULT_PING_INTERVAL : pingInterval;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        schedulePing(ctx);
    }

    private void schedulePing(ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {
            Channel channel = ctx.channel();
            if (channel.isActive()) {
                log.debug("[{}] Send a PingPacket", HealthyChecker.class.getSimpleName());
                channel.writeAndFlush(new PingPacket());
                schedulePing(ctx);
            }
        }, pingInterval, TimeUnit.SECONDS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().schedule(() -> {
            log.info("[{}] Try to reconnecting...", HealthyChecker.class.getSimpleName());
            client.connect();
        }, 5, TimeUnit.SECONDS);
        ctx.fireChannelInactive();
    }

}