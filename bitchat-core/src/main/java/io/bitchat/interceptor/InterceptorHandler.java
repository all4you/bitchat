package io.bitchat.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import io.bitchat.protocol.Packet;
import io.bitchat.protocol.Payload;
import io.bitchat.protocol.PayloadFactory;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @author houyi
 **/
public class InterceptorHandler {

    public static Payload preHandle(Channel channel, Packet packet) {
        List<Interceptor> interceptors = InterceptorProvider.getInterceptors();
        if (CollectionUtil.isEmpty(interceptors)) {
            return PayloadFactory.newSuccessPayload();
        }
        for (Interceptor interceptor : interceptors) {
            Payload payload = interceptor.preHandle(channel, packet);
            if (!payload.isSuccess()) {
                return payload;
            }
        }
        return PayloadFactory.newSuccessPayload();
    }


}
