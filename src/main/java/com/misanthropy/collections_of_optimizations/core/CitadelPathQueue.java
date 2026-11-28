package com.misanthropy.collections_of_optimizations.core;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public final class CitadelPathQueue {

    public static final int CAPACITY = 256;

    private static final long COOLDOWN_MILLIS = 1000L;
    private static final long FORGET_MILLIS = 60000L;
    private static final int FORGET_ABOVE = 4096;
    private static final Map<UUID, Long> REJECTED = new ConcurrentHashMap<>();

    private CitadelPathQueue() {
    }

    public static boolean full(ExecutorService service) {
        return service instanceof ThreadPoolExecutor executor && executor.getQueue().remainingCapacity() == 0;
    }

    public static boolean cooling(UUID id) {
        if (id == null) {
            return false;
        }
        Long since = REJECTED.get(id);
        return since != null && System.currentTimeMillis() - since < COOLDOWN_MILLIS;
    }

    public static void reject(UUID id) {
        if (id == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (REJECTED.size() > FORGET_ABOVE) {
            REJECTED.values().removeIf(since -> now - since > FORGET_MILLIS);
        }
        REJECTED.put(id, now);
    }
}
