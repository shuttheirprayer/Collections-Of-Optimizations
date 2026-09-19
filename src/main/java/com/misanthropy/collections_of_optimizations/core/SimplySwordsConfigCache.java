package com.misanthropy.collections_of_optimizations.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class SimplySwordsConfigCache {

    private static final long UNSET = Long.MIN_VALUE;

    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicLong>> STAMPS = new ConcurrentHashMap<>();

    private SimplySwordsConfigCache() {
    }

    public static boolean stillFresh(String type, String category, int intervalMillis) {
        ConcurrentHashMap<String, AtomicLong> byCategory = STAMPS.computeIfAbsent(type, key -> new ConcurrentHashMap<>());
        AtomicLong stamp = byCategory.computeIfAbsent(category, key -> new AtomicLong(UNSET));
        long last = stamp.get();
        long now = System.currentTimeMillis();
        if (last != UNSET && now - last >= 0 && now - last < intervalMillis) {
            return true;
        }
        stamp.set(now);
        return false;
    }
}
