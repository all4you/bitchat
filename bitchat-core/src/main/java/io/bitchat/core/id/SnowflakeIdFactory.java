package io.bitchat.core.id;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import io.bitchat.lang.config.ConfigFactory;
import io.bitchat.lang.config.SnowflakeConfig;

/**
 * @author houyi
 */
public class SnowflakeIdFactory implements IdFactory {

    private Snowflake snowflake;

    private SnowflakeIdFactory() {
        this(null);
    }

    private SnowflakeIdFactory(Long workerId) {
        SnowflakeConfig config = ConfigFactory.getConfig(SnowflakeConfig.class);
        Long realWorkerId = workerId != null ? workerId : config.workerId();
        this.snowflake = IdUtil.createSnowflake(realWorkerId, config.dataCenterId());
    }

    public static IdFactory getInstance() {
        return Singleton.get(SnowflakeIdFactory.class);
    }

    public static IdFactory getInstance(Long workerId) {
        return Singleton.get(SnowflakeIdFactory.class, workerId);
    }


    @Override
    public long nextId() {
        return snowflake.nextId();
    }


}
