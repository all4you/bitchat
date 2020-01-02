package io.bitchat.server;

import io.bitchat.lang.constants.ServiceName;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.PayloadFactory;
import io.bitchat.packet.processor.AbstractRequestProcessor;
import io.bitchat.packet.processor.Processor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
@Processor(name = ServiceName.HEART_BEAT)
public class HeartBeatProcessor extends AbstractRequestProcessor {

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        log.debug("Return a Pong");
        return PayloadFactory.newSuccessPayload();
    }

}
