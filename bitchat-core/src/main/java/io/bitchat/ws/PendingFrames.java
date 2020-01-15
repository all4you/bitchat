package io.bitchat.ws;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author houyi
 */
@Slf4j
public class PendingFrames {

    private static Map<String, CompletableFuture<Frame>> pendingFrames = new ConcurrentHashMap<>();

    public static void add(String id, CompletableFuture<Frame> promise) {
        pendingFrames.putIfAbsent(id, promise);
    }

    public static CompletableFuture<Frame> remove(String id) {
        return pendingFrames.remove(id);
    }

}
