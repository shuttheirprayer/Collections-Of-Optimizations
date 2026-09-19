package com.misanthropy.collections_of_optimizations.core;

import com.Polarice3.Goety.common.effects.brew.BrewEffects;

import java.util.concurrent.TimeUnit;

public final class GoetyBrewEffectsCache {

    private static final long TTL_NANOS = TimeUnit.SECONDS.toNanos(5);
    private static BrewEffects cached;
    private static long builtAt;

    private GoetyBrewEffectsCache() {
    }

    public static synchronized BrewEffects get() {
        long now = System.nanoTime();
        if (cached == null || now - builtAt > TTL_NANOS) {
            cached = new BrewEffects();
            builtAt = now;
        }
        return cached;
    }
}
