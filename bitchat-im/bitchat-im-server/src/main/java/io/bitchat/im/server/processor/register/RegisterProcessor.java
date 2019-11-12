package io.bitchat.im.server.processor.register;

import io.bitchat.im.BaseResult;
import io.bitchat.im.ImServiceName;
import io.bitchat.im.server.BeanUtil;
import io.bitchat.im.server.service.user.UserService;
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
@Processor(name = ImServiceName.REGISTER)
public class RegisterProcessor extends AbstractRequestProcessor {

    private UserService userService;

    public RegisterProcessor() {
        this.userService = BeanUtil.getBean(UserService.class);
    }

    @Override
    public Payload doProcess(ChannelHandlerContext ctx, Map<String, Object> params) {
        // transfer map to bean
        RegisterRequest loginRequest = mapToBean(params, RegisterRequest.class);
        BaseResult baseResult = userService.register(loginRequest);
        return baseResult.isSuccess() ?
                PayloadFactory.newSuccessPayload() :
                PayloadFactory.newErrorPayload(baseResult.getErrorCode(), baseResult.getErrorMsg());
    }


}
