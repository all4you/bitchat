package io.bitchat.packet.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import io.bitchat.packet.Packet;
import io.bitchat.packet.Payload;
import io.bitchat.packet.factory.PayloadFactory;
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
