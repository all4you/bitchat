package io.bitchat.packet.interceptor;

import java.util.List;

/**
 * User can build an active interceptor list
 *
 * @author houyi
 **/
public interface InterceptorBuilder {

    /**
     * build the interceptor
     *
     * @return the list of the interceptor
     */
    List<Interceptor> build();

}
