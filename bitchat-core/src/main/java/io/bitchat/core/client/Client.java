package io.bitchat.core.client;

import io.bitchat.core.Node;
import io.bitchat.core.protocol.packet.Packet;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * A client which can communicate with Server
 * </p>
 *
 * @author houyi
 */
public interface Client extends Node {

    /**
     * connect server
     */
    void connect();

    /**
     * send request to server
     *
     * @param request the request packet
     * @return the response future
     */
    CompletableFuture<Packet> sendRequest(Packet request);

}
