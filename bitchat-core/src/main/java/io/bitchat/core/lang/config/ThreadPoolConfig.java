package io.bitchat.core.lang.config;

import org.aeonbits.owner.Config;

/**
 * @author houyi
 */
@Config.Sources({"classpath:config/thread-pool-config.properties"})
public interface ThreadPoolConfig extends Config {

    /**
     * get the core pool size
     *
     * @return the core pool size
     */
    @DefaultValue("10")
    int corePoolSize();

    /**
     * get the max pool size
     *
     * @return the max pool size
     */
    @DefaultValue("20")
    int maxPoolSize();

    /**
     * get the temporary thread idle time
     * unit: seconds
     *
     * @return the temporary thread idle time
     */
    @DefaultValue("5")
    long idealTime();

    /**
     * get the blocking queue cap
     *
     * @return the blocking queue cap
     */
    @DefaultValue("100")
    int blockingQueueCap();

}
