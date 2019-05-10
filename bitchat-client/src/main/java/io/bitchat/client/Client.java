package io.bitchat.client;

import io.bitchat.protocol.packet.Packet;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * A client which can communicate with Server
 * </p>
 *
 * @author houyi
 */
public interface Client {

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
