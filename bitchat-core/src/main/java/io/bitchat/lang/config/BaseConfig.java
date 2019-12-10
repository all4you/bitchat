package io.bitchat.lang.config;

import org.aeonbits.owner.Config;

/**
 * @author houyi
 */
@Config.Sources({"classpath:config/base-config.properties"})
public interface BaseConfig extends Config {

    /**
     * get the base package
     *
     * @return the base package
     */
    @DefaultValue("io.bitchat")
    String basePackage();

    /**
     * get the server port
     *
     * @return the server port
     */
    @DefaultValue("8864")
    int serverPort();

    /**
     * get the server internal error
     *
     * @return the server internal error
     */
    @DefaultValue("Server Internal Error")
    String serverInternalError();

    /**
     * get the readerIdleTime
     *
     * @return the readerIdleTime
     */
    @DefaultValue("60")
    int readerIdleTime();

    /**
     * get the pingInterval
     *
     * @return the pingInterval
     */
    @DefaultValue("20")
    int pingInterval();

}
