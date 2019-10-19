package io.bitchat.im.client.cmd.msg;

import io.bitchat.im.ImServiceName;
import io.bitchat.protocol.AbstractCommandProcessor;
import io.bitchat.protocol.Processor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
@Processor(name = ImServiceName.PUSH_MSG)
public class PushMsgProcessor extends AbstractCommandProcessor {

    @Override
    public void doProcess(ChannelHandlerContext ctx, Map<String, Object> content) {
        // transfer map to bean
        PushMsgCmd pushMsgCmd = mapToBean(content, PushMsgCmd.class);
        Long partnerId = pushMsgCmd.getPartnerId();
        String partnerName = pushMsgCmd.getPartnerName();
        String msg = pushMsgCmd.getMsg();
        System.out.println(String.format("%s(%d):\t%s", partnerName, partnerId, msg));
        System.out.println("bitchat> ");
    }


}
