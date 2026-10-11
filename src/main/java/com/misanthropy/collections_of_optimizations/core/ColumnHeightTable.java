package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

public final class ColumnHeightTable {

    public static final int ABSENT = Integer.MIN_VALUE;

    private static final int CAPACITY = 1 << 15;
    private static final int MAX_TABLES = 4;
    private static final int COORD_LIMIT = 1 << 25;
    private static final long COORD_MASK = (1L << 26) - 1L;

    public final RandomState randomState;
    public final int minBuildHeight;
    public final int maxBuildHeight;
    private final Long2IntLinkedOpenHashMap heights = new Long2IntLinkedOpenHashMap(1024);

    public ColumnHeightTable(RandomState randomState, int minBuildHeight, int maxBuildHeight) {
        this.randomState = randomState;
        this.minBuildHeight = minBuildHeight;
        this.maxBuildHeight = maxBuildHeight;
        this.heights.defaultReturnValue(ABSENT);
    }

    public static ColumnHeightTable find(ColumnHeightTable[] tables, RandomState randomState, int minBuildHeight, int maxBuildHeight) {
        if (tables == null) {
            return null;
        }
        for (ColumnHeightTable table : tables) {
            if (table.randomState == randomState && table.minBuildHeight == minBuildHeight && table.maxBuildHeight == maxBuildHeight) {
                return table;
            }
        }
        return null;
    }

    public static ColumnHeightTable[] with(ColumnHeightTable[] tables, ColumnHeightTable table) {
        int keep = tables == null ? 0 : Math.min(tables.length, MAX_TABLES - 1);
        ColumnHeightTable[] next = new ColumnHeightTable[keep + 1];
        next[0] = table;
        if (keep > 0) {
            System.arraycopy(tables, 0, next, 1, keep);
        }
        return next;
    }

    public static long key(int x, int z, Heightmap.Types type) {
        if (x < -COORD_LIMIT || x >= COORD_LIMIT || z < -COORD_LIMIT || z >= COORD_LIMIT || type == null) {
            return Long.MIN_VALUE;
        }
        return (long) type.ordinal() << 52 | (x & COORD_MASK) << 26 | (z & COORD_MASK);
    }

    public synchronized int get(long key) {
        return this.heights.getAndMoveToLast(key);
    }

    public synchronized void put(long key, int value) {
        this.heights.put(key, value);
        if (this.heights.size() > CAPACITY) {
            this.heights.removeFirstInt();
        }
    }
}
