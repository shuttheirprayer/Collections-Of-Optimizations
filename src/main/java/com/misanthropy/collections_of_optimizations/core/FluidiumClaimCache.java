package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class FluidiumClaimCache {

    public static final int MISS = -1;

    private static final int SLOTS = 256;

    private static final long[] KEYS = new long[SLOTS];
    private static final long[] STAMPS = new long[SLOTS];
    private static final boolean[] VALUES = new boolean[SLOTS];

    private static Object dimension;
    private static long gameTime = Long.MIN_VALUE;
    private static long generation = 1L;

    private FluidiumClaimCache() {
    }

    public static boolean usable(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        MinecraftServer server = serverLevel.getServer();
        return server != null && server.isSameThread();
    }

    public static int lookup(Level level, BlockPos pos) {
        long stamp = stamp(level);
        long key = key(pos);
        int slot = index(key);
        if (STAMPS[slot] != stamp || KEYS[slot] != key) {
            return MISS;
        }
        return VALUES[slot] ? 1 : 0;
    }

    public static void store(Level level, BlockPos pos, boolean claimed) {
        long stamp = stamp(level);
        long key = key(pos);
        int slot = index(key);
        KEYS[slot] = key;
        VALUES[slot] = claimed;
        STAMPS[slot] = stamp;
    }

    private static long stamp(Level level) {
        Object dim = level.dimension();
        long time = level.getGameTime();
        if (dim != dimension || time != gameTime) {
            dimension = dim;
            gameTime = time;
            generation++;
        }
        return generation;
    }

    private static long key(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private static int index(long key) {
        long hash = key * 0x9E3779B97F4A7C15L;
        hash ^= hash >>> 32;
        return (int) hash & (SLOTS - 1);
    }
}
