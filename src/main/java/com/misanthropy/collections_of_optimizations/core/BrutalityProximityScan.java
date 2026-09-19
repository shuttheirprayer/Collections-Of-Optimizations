package com.misanthropy.collections_of_optimizations.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class BrutalityProximityScan {

    private static final Map<Class<?>, Boolean> RESULTS = new HashMap<>();
    private static long clientTick;
    private static long bucket = Long.MIN_VALUE;

    private BrutalityProximityScan() {
    }

    public static void advance() {
        clientTick++;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean query(Stream stream, Predicate predicate, int interval) {
        if (stream == null || predicate == null) {
            return false;
        }
        if (interval <= 1) {
            return stream.anyMatch(predicate);
        }
        long current = clientTick / interval;
        if (current != bucket) {
            bucket = current;
            RESULTS.clear();
        }
        Class<?> key = predicate.getClass();
        Boolean cached = RESULTS.get(key);
        if (cached != null) {
            return cached;
        }
        boolean value = stream.anyMatch(predicate);
        RESULTS.put(key, value);
        return value;
    }
}
