package io.bitchat.core.id;

import cn.hutool.core.lang.Singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author houyi
 */
public class StandaloneMemoryIdFactory implements IdFactory {

    private static AtomicLong id = new AtomicLong(1);

    private StandaloneMemoryIdFactory() {

    }

    public static IdFactory getInstance() {
        return Singleton.get(StandaloneMemoryIdFactory.class);
    }

    @Override
    public long nextId() {
        return id.getAndIncrement();
    }


}
