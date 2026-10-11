package com.misanthropy.collections_of_optimizations.core;

import com.github.alexmodguy.alexscaves.server.misc.VoronoiGenerator;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public final class CaveBiomeQuadCache {

    private static final int LIMIT = 4096;

    public static final VoronoiGenerator.VoronoiInfo ABSENT =
            new VoronoiGenerator.VoronoiInfo(0.0D, 0.0D, 0.0D, null, null);

    private static final ThreadLocal<CaveBiomeQuadCache> LOCAL = ThreadLocal.withInitial(CaveBiomeQuadCache::new);

    private static volatile int generation;

    private final Long2ObjectOpenHashMap<VoronoiGenerator.VoronoiInfo> entries = new Long2ObjectOpenHashMap<>();

    private long seed;
    private double widthRandomness;
    private int seenGeneration = -1;

    private CaveBiomeQuadCache() {
    }

    public static CaveBiomeQuadCache get() {
        return LOCAL.get();
    }

    public static void invalidate() {
        generation++;
    }

    public VoronoiGenerator.VoronoiInfo lookup(long worldSeed, double widthRandomness, int x, int z) {
        reset(worldSeed, widthRandomness);
        return this.entries.get(key(x, z));
    }

    public void store(long worldSeed, double widthRandomness, int x, int z, VoronoiGenerator.VoronoiInfo value) {
        reset(worldSeed, widthRandomness);
        if (this.entries.size() >= LIMIT) {
            this.entries.clear();
        }
        this.entries.put(key(x, z), value == null ? ABSENT : value);
    }

    private void reset(long worldSeed, double widthRandomness) {
        int current = generation;
        if (this.seenGeneration != current || this.seed != worldSeed
                || Double.doubleToRawLongBits(this.widthRandomness) != Double.doubleToRawLongBits(widthRandomness)) {
            this.entries.clear();
            this.seenGeneration = current;
            this.seed = worldSeed;
            this.widthRandomness = widthRandomness;
        }
    }

    private static long key(int x, int z) {
        return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
    }
}
