package io.bitchat.server;

/**
 * @author houyi
 */
public interface ServerMode {

    /**
     * standalone mode
     */
    int STAND_ALONE = 1;

    /**
     * cluster mode
     */
    int CLUSTER = 2;

}
