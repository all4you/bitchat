package io.bitchat.packet.processor;

import io.bitchat.packet.Command;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 * A command processor
 * is aim to process the command
 * </p>
 *
 * @author houyi
 */
public interface CommandProcessor {

    /**
     * process the command
     *
     * @param ctx     the ctx
     * @param command the command
     */
    void process(ChannelHandlerContext ctx, Command command);

}
