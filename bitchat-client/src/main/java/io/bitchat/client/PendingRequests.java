package io.bitchat.client;

import io.bitchat.protocol.packet.Packet;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author houyi
 */
@Slf4j
public class PendingRequests {

    private static Map<Long, CompletableFuture<Packet>> pendingRequests = new ConcurrentHashMap<>();

    public static void add(Long id, CompletableFuture<Packet> promise) {
        pendingRequests.putIfAbsent(id, promise);
    }

    public static CompletableFuture<Packet> remove(Long id) {
        return pendingRequests.remove(id);
    }

}
