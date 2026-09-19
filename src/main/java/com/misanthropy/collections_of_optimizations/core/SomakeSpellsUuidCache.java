package com.misanthropy.collections_of_optimizations.core;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SomakeSpellsUuidCache {

    private static final int MAX_ENTRIES = 8192;

    private static final ConcurrentHashMap<String, UUID> CACHE = new ConcurrentHashMap<>();

    private SomakeSpellsUuidCache() {
    }

    public static UUID get(String key) {
        UUID cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        UUID computed = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
        if (CACHE.size() < MAX_ENTRIES) {
            UUID existing = CACHE.putIfAbsent(key, computed);
            if (existing != null) {
                return existing;
            }
        }
        return computed;
    }
}
