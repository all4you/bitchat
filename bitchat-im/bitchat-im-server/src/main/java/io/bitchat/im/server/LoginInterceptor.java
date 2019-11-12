package io.bitchat.im.server;

import io.bitchat.im.ImServiceName;
import io.bitchat.im.connection.ConnectionUtil;
import io.bitchat.interceptor.Interceptor;
import io.bitchat.lang.constants.ResultCode;
import io.bitchat.lang.constants.ServiceName;
import io.bitchat.protocol.Packet;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
import io.netty.channel.Channel;

/**
 * @author houyi
 */
public class LoginInterceptor extends Interceptor {

    @Override
    public Payload preHandle(Channel channel, Packet packet) {
        boolean shouldCheckLogin = false;
        String serviceName = packet.getRequest().getServiceName();
        if (!(ImServiceName.REGISTER.equals(serviceName) || ImServiceName.LOGIN.equals(serviceName) || ServiceName.HEART_BEAT.equals(serviceName))) {
            shouldCheckLogin = true;
        }
        // if not logged in
        if (shouldCheckLogin && !ConnectionUtil.hasLogin(channel)) {
            return PayloadFactory.newErrorPayload(ResultCode.BIZ_FAIL.getCode(), "Not Logged in yet!");
        }
        // if already logged in
        if (ImServiceName.LOGIN.equals(serviceName) && ConnectionUtil.hasLogin(channel)) {
            return PayloadFactory.newErrorPayload(ResultCode.BIZ_FAIL.getCode(), "Already Logged in!");
        }
        return PayloadFactory.newSuccessPayload();
    }


}
