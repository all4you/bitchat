package io.bitchat.core.lang.config;

import org.aeonbits.owner.Config;

/**
 * @author houyi
 */
@Config.Sources({"classpath:config/snowflake-config.properties"})
public interface SnowflakeConfig extends Config {

    /**
     * get the worker id of the server
     *
     * @return the worker id
     */
    @DefaultValue("1")
    long workerId();

    /**
     * get the data center id of the server
     *
     * @return the data center id
     */
    @DefaultValue("1")
    long dataCenterId();

}
