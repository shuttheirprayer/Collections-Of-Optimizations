package com.misanthropy.collections_of_optimizations.core;

public final class TmgRayParticleBudget {

    private static int stamp = Integer.MIN_VALUE;
    private static int spent;

    private TmgRayParticleBudget() {
    }

    public static boolean allow(int budget, int estimate) {
        int now = ClientTickStamp.current();
        if (now != stamp) {
            stamp = now;
            spent = 0;
        }
        if (spent >= budget) {
            return false;
        }
        spent += estimate;
        return true;
    }
}
