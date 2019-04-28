package io.bitchat.core.id;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import io.bitchat.core.lang.config.ConfigFactory;
import io.bitchat.core.lang.config.SnowflakeConfig;

/**
 * @author houyi
 */
public class SnowflakeIdFactory implements IdFactory {

    private static Snowflake snowflake;

    static {
        SnowflakeConfig config = ConfigFactory.getConfig(SnowflakeConfig.class);
        snowflake = IdUtil.createSnowflake(config.workerId(), config.dataCenterId());
    }

    private SnowflakeIdFactory() {

    }

    public static IdFactory getInstance() {
        return Singleton.get(SnowflakeIdFactory.class);
    }


    @Override
    public long nextId() {
        return snowflake.nextId();
    }


}
