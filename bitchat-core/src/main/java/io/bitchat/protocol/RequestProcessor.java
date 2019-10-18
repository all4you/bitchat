package io.bitchat.protocol;

import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 * A request processor
 * is aim to process the request
 * and return a payload
 * </p>
 *
 * @author houyi
 */
public interface RequestProcessor {

    /**
     * process the request
     *
     * @param ctx     the ctx
     * @param request the request
     * @return the response
     */
    Payload process(ChannelHandlerContext ctx, Request request);

}
