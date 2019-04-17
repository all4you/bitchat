package io.bitchat.core.lang.config;

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

}
