package io.bitchat.core.router;

import io.bitchat.core.Node;

/**
 * <p>
 * A router server node
 * </p>
 *
 * @author houyi
 */
public interface RouterServer extends Node {

    /**
     * start the router server
     */
    void start();

    /**
     * stop the router server
     */
    void stop();

}
