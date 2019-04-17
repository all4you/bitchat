package io.bitchat.core.executor;

import io.bitchat.core.lang.config.ConfigFactory;
import io.bitchat.core.lang.config.ThreadPoolConfig;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author houyi
 */
@Slf4j
public abstract class AbstractExecutor<T> implements Executor<T> {

    private java.util.concurrent.Executor eventExecutor;

    public AbstractExecutor() {
        this(null);
    }

    public AbstractExecutor(java.util.concurrent.Executor eventExecutor) {
        this.eventExecutor = eventExecutor == null ? EventExecutorHolder.eventExecutor : eventExecutor;
    }

    @Override
    public T execute(Object... request) {
        return doExecute(request);
    }

    @Override
    public Future<T> asyncExecute(Promise<T> promise, Object... request) {
        if (promise == null) {
            throw new IllegalArgumentException("promise should not be null");
        }
        // async execute
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    T response = doExecute(request);
                    promise.setSuccess(response);
                } catch (Exception e) {
                    promise.setFailure(e);
                }
            }
        });
        // return the promise back
        return promise;
    }

    /**
     * do actual execute
     *
     * @param request the request
     * @return the response
     */
    public abstract T doExecute(Object... request);

    private static final class EventExecutorHolder {
        private static ThreadPoolConfig config = ConfigFactory.getConfig(ThreadPoolConfig.class);
        private static java.util.concurrent.Executor eventExecutor = new ThreadPoolExecutor(
                config.corePoolSize(),
                config.maxPoolSize(),
                config.idealTime(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(config.blockingQueueCap()),
                new DefaultThreadFactory("event-executor-pool", true));
    }

}
