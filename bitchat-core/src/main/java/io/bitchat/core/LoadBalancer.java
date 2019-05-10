package io.bitchat.core;


/**
 * <p>
 * A load balancer can return a healthy server
 * and make sure the servers are load balanced
 * </p>
 *
 * @author houyi
 */
public interface LoadBalancer {

    /**
     * get a healthy server
     *
     * @return a healthy server
     */
    ServerAttr nextServer();

}
