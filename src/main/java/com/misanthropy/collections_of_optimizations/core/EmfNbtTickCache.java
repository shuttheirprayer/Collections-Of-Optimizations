package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EmfNbtTickCache {

    private static final Map<String, Map<String, Object2ByteOpenHashMap<UUID>>> RESULTS = new HashMap<>();
    private static int stamp = -1;

    private EmfNbtTickCache() {
    }

    public static Object2ByteOpenHashMap<UUID> forQuery(String nbtKey, String nbtQuery) {
        int now = ClientTickStamp.current();
        if (now != stamp) {
            stamp = now;
            for (Map<String, Object2ByteOpenHashMap<UUID>> byQuery : RESULTS.values()) {
                for (Object2ByteOpenHashMap<UUID> byEntity : byQuery.values()) {
                    byEntity.clear();
                }
            }
        }
        return RESULTS.computeIfAbsent(nbtKey, k -> new HashMap<>()).computeIfAbsent(nbtQuery, k -> {
            Object2ByteOpenHashMap<UUID> byEntity = new Object2ByteOpenHashMap<>();
            byEntity.defaultReturnValue((byte) -1);
            return byEntity;
        });
    }
}
