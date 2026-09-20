package com.misanthropy.collections_of_optimizations.core;

public final class CorpseDeathSweepState {

    private static long lastSweep;

    private CorpseDeathSweepState() {
    }

    public static boolean allow(int intervalSeconds) {
        if (intervalSeconds <= 0) {
            return true;
        }
        long now = System.currentTimeMillis();
        long last = lastSweep;
        if (last != 0L && now - last < intervalSeconds * 1000L) {
            return false;
        }
        lastSweep = now;
        return true;
    }
}
