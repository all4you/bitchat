package io.bitchat.server;

import cn.hutool.core.collection.CollectionUtil;
import io.bitchat.core.Carrier;
import io.bitchat.protocol.packet.Packet;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @author houyi
 **/
public class InterceptorHandler {

    public static Carrier<Packet> preHandle(Channel channel, Packet packet) {
        List<Interceptor> interceptors = InterceptorProvider.getInterceptors();
        if (CollectionUtil.isEmpty(interceptors)) {
            return Interceptor.SUCCESS;
        }
        for (Interceptor interceptor : interceptors) {
            Carrier<Packet> carrier = interceptor.preHandle(channel, packet);
            if (!carrier.isSuccess()) {
                return carrier;
            }
        }
        return Interceptor.SUCCESS;
    }


}
