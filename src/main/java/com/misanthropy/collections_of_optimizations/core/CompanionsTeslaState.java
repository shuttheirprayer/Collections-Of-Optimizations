package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.CoOConfig;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CompanionsTeslaState {

    public interface Network {

        void coo$rebuildNow();

        void coo$dropRemovedBlockEntities();
    }

    private static final Set<Network> TRACKED = ConcurrentHashMap.newKeySet();
    private static final Set<Network> DIRTY = ConcurrentHashMap.newKeySet();

    private static boolean rebuilding;
    private static int pruneTicks;

    private CompanionsTeslaState() {
    }

    public static void track(Network network) {
        TRACKED.add(network);
    }

    public static void clear() {
        TRACKED.clear();
        DIRTY.clear();
        rebuilding = false;
        pruneTicks = 0;
    }

    public static boolean defer(Network network) {
        if (rebuilding || !CoOConfig.companionsDeferTeslaRebuild) {
            return false;
        }
        DIRTY.add(network);
        return true;
    }

    public static void serverTick() {
        if (!DIRTY.isEmpty()) {
            rebuilding = true;
            try {
                for (Network network : DIRTY) {
                    network.coo$rebuildNow();
                }
            } finally {
                DIRTY.clear();
                rebuilding = false;
            }
        }

        int interval = CoOConfig.companionsTeslaPruneInterval;
        if (interval <= 0 || TRACKED.isEmpty()) {
            return;
        }

        if (++pruneTicks >= interval) {
            pruneTicks = 0;
            for (Network network : TRACKED) {
                network.coo$dropRemovedBlockEntities();
            }
        }
    }
}
