package io.bitchat.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * A ChannelHandler which will check
 * the idle state of each Channel
 * </p>
 *
 * <p>
 * This ChannelHandler can be added to
 * Client or Server Channel Pipeline
 * </p>
 *
 * <p>
 * But one thing should be confirmed is
 * that this ChannelHandler can not be
 * {@link io.netty.channel.ChannelHandler.Sharable}
 * </p>
 *
 * @author houyi
 */
@Slf4j
public class IdleStateChecker extends IdleStateHandler {

    private static final int DEFAULT_READER_IDLE_TIME = 15;

    private int readerTime;

    public IdleStateChecker(int readerIdleTime) {
        super(readerIdleTime == 0 ? DEFAULT_READER_IDLE_TIME : readerIdleTime, 0, 0, TimeUnit.SECONDS);
        readerTime = readerIdleTime == 0 ? DEFAULT_READER_IDLE_TIME : readerIdleTime;
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        log.warn("[{}] Hasn't read data after {} seconds, will close the channel:{}", IdleStateChecker.class.getSimpleName(), readerTime, ctx.channel());
        ctx.channel().close();
    }

}