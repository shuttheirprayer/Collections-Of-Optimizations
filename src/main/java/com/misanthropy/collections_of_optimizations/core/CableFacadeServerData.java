package com.misanthropy.collections_of_optimizations.core;

import com.portingdeadmods.cable_facades.data.CableFacadeSavedData;
import net.minecraft.server.level.ServerLevel;

import java.lang.ref.WeakReference;

public final class CableFacadeServerData {

    private static final int LIMIT = 32;
    private static volatile Entry[] entries = new Entry[0];

    private CableFacadeServerData() {
    }

    public static boolean noFacades(ServerLevel level) {
        Entry[] current = entries;
        for (Entry entry : current) {
            if (entry.level.get() == level) {
                return entry.data.isEmpty();
            }
        }
        CableFacadeSavedData data = CableFacadeSavedData.get(level);
        int keep = 0;
        Entry[] next = new Entry[Math.min(current.length, LIMIT - 1) + 1];
        for (int i = Math.max(0, current.length - (LIMIT - 1)); i < current.length; i++) {
            if (current[i].level.get() != null) {
                next[keep++] = current[i];
            }
        }
        next[keep++] = new Entry(new WeakReference<>(level), data);
        entries = keep == next.length ? next : java.util.Arrays.copyOf(next, keep);
        return data.isEmpty();
    }

    private record Entry(WeakReference<ServerLevel> level, CableFacadeSavedData data) {
    }
}
