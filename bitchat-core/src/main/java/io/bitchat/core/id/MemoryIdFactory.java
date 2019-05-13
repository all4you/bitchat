package io.bitchat.core.id;

import cn.hutool.core.lang.Singleton;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author houyi
 */
public class MemoryIdFactory implements IdFactory {

    private static AtomicLong id = new AtomicLong(1);

    private MemoryIdFactory() {

    }

    public static IdFactory getInstance() {
        return Singleton.get(MemoryIdFactory.class);
    }

    @Override
    public long nextId() {
        return id.getAndIncrement();
    }


}
