package io.bitchat.im.client.cmd.msg;

import io.bitchat.im.ImServiceName;
import io.bitchat.protocol.AbstractRequestProcessor;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
import io.bitchat.protocol.Processor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
@Processor(serviceName = ImServiceName.PUSH_MSG)
public class PushMsgProcessor extends AbstractRequestProcessor {

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        // transfer map to bean
        PushMsgCmd pushMsgCmd = mapToBean(params, PushMsgCmd.class);
        Long partnerId = pushMsgCmd.getPartnerId();
        String partnerName = pushMsgCmd.getPartnerName();
        String msg = pushMsgCmd.getMsg();
        System.out.println(String.format("%s(%d):\t%s", partnerName, partnerId, msg));
        System.out.println("bitchat> ");
        return PayloadFactory.newSuccessPayload();
    }


}
