package io.bitchat.core;

import io.bitchat.core.id.IdFactory;
import io.bitchat.core.id.SnowflakeIdFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author houyi
 */
@Slf4j
public class IdTest {

    private IdFactory idFactory1;
    private IdFactory idFactory2;

    @Before
    public void before() {
        idFactory1 = SnowflakeIdFactory.getInstance(1L);
        idFactory2 = SnowflakeIdFactory.getInstance(2L);
    }

    @Test
    public void testDeserialize() {
        log.info("idFactory1={}", idFactory1);
        log.info("idFactory2={}", idFactory2);
        Set<Long> set = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            Long id1 = idFactory1.nextId();
            Long id2 = idFactory2.nextId();
            log.info("idFactory1 nextId={}", id1);
            log.info("idFactory2 nextId={}", id2);
            set.add(id1);
            set.add(id2);
        }
        log.info("set size={}", set.size());
    }

}
