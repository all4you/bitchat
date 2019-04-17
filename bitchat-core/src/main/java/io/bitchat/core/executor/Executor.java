package io.bitchat.core.executor;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

/**
 * @author houyi
 */
public interface Executor<T> {

    /**
     * handle the request and return the response
     *
     * @param request the request
     * @return the response
     */
    T execute(Object... request);

    /**
     * handle the request async and return a future directly
     *
     * @param promise a promise
     * @param request the request
     * @return the async result
     */
    Future<T> asyncExecute(Promise<T> promise, Object... request);

}
