package io.bitchat.packet.processor;

import io.bitchat.packet.Command;
import io.bitchat.packet.ctx.RequestProcessorContext;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * <p>
 * An abstract CommandProcessor
 * Every Class extends AbstractCommandProcessor should have a NoArgument Constructor
 * see {@link RequestProcessorContext#initRequestProcessor()}
 * </p>
 *
 * @author houyi
 */
@Slf4j
public abstract class AbstractCommandProcessor implements CommandProcessor {

    @Override
    public void process(ChannelHandlerContext ctx, Command command) {
        try {
            doProcess(ctx, command.getContent());
        } catch (Exception e) {
            log.error("process occurred error, cause={}", e.getMessage(), e);
        }
    }

    /**
     * do process
     *
     * @param content the command content
     */
    public abstract void doProcess(ChannelHandlerContext ctx, Map<String, Object> content);

}
