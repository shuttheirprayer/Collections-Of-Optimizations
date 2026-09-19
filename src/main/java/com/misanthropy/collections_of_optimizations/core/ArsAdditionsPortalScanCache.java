package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public final class ArsAdditionsPortalScanCache {

    private static final int SIZE = 256;
    private static final int MASK = SIZE - 1;
    private static final int RESET_INTERVAL = 6000;

    private static final Object[] MANAGERS = new Object[SIZE];
    private static final long[] POSITIONS = new long[SIZE];

    private static int lookups;

    private ArsAdditionsPortalScanCache() {
    }

    private static int slot(long packed) {
        long mixed = packed * 0x9E3779B97F4A7C15L;
        return (int) ((mixed >>> 40) & MASK);
    }

    public static StructureStart cachedMiss(StructureManager manager, BlockPos pos) {
        if (manager == null || pos == null) {
            return null;
        }
        if (++lookups >= RESET_INTERVAL) {
            lookups = 0;
            clear();
            return null;
        }
        long packed = pos.asLong();
        int index = slot(packed);
        if (MANAGERS[index] == manager && POSITIONS[index] == packed) {
            return StructureStart.INVALID_START;
        }
        return null;
    }

    public static void storeMiss(StructureManager manager, BlockPos pos) {
        if (manager == null || pos == null) {
            return;
        }
        long packed = pos.asLong();
        int index = slot(packed);
        MANAGERS[index] = manager;
        POSITIONS[index] = packed;
    }

    public static void clear() {
        for (int i = 0; i < SIZE; i++) {
            MANAGERS[i] = null;
            POSITIONS[i] = 0L;
        }
    }
}
