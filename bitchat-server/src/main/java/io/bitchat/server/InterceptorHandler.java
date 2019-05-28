package io.bitchat.server;

import cn.hutool.core.collection.CollectionUtil;
import io.bitchat.core.Carrier;
import io.bitchat.protocol.packet.Packet;

import java.util.List;

/**
 * @author houyi
 **/
public class InterceptorHandler {

    public static Carrier<Packet> preHandle(Packet packet) {
        List<Interceptor> interceptors = InterceptorProvider.getInterceptors();
        if (CollectionUtil.isEmpty(interceptors)) {
            return Interceptor.SUCCESS;
        }
        for (Interceptor interceptor : interceptors) {
            Carrier<Packet> carrier = interceptor.preHandle(packet);
            if (!carrier.isSuccess()) {
                return carrier;
            }
        }
        return Interceptor.SUCCESS;
    }

    @Deprecated
    public static void postHandle(Packet packet) {
        List<Interceptor> interceptors = InterceptorProvider.getInterceptors();
        if (CollectionUtil.isEmpty(interceptors)) {
            return;
        }
        for (Interceptor interceptor : interceptors) {
            interceptor.postHandle(packet);
        }
    }


}
