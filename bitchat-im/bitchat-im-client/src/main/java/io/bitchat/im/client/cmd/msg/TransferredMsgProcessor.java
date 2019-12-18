package io.bitchat.im.client.cmd.msg;

import cn.hutool.core.bean.BeanUtil;
import io.bitchat.im.ImServiceName;
import io.bitchat.packet.processor.AbstractCommandProcessor;
import io.bitchat.packet.processor.Processor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author houyi
 */
@Slf4j
@Processor(name = ImServiceName.TRANSFER_MSG)
public class TransferredMsgProcessor extends AbstractCommandProcessor {

    @Override
    public void doProcess(ChannelHandlerContext ctx, Map<String, Object> content) {
        // transfer map to bean
        TransferMsgCmd pushMsgCmd = BeanUtil.mapToBean(content, TransferMsgCmd.class, false);
        Long partnerId = pushMsgCmd.getPartnerId();
        String partnerName = pushMsgCmd.getPartnerName();
        String msg = pushMsgCmd.getMsg();
        System.out.println(String.format("%s(%d):\t%s", partnerName, partnerId, msg));
        System.out.println("bitchat> ");
    }


}
