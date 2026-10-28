package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

public final class BiomeLookupCache {
    private static final int BITS = 18;
    private static final int MASK = (1 << BITS) - 1;
    private static final int XZ_LIMIT = 1 << 23;
    private static final int Y_LIMIT = 1 << 11;

    private record Entry(Climate.Sampler sampler, long key, Holder<Biome> value) {
    }

    private final Entry[] entries = new Entry[1 << BITS];

    public static boolean cacheable(int x, int y, int z) {
        return x > -XZ_LIMIT && x < XZ_LIMIT && z > -XZ_LIMIT && z < XZ_LIMIT && y > -Y_LIMIT && y < Y_LIMIT;
    }

    private static long key(int x, int y, int z) {
        return ((long) (x & 0xFFFFFF) << 36) | ((long) (y & 0xFFF) << 24) | (z & 0xFFFFFF);
    }

    public Holder<Biome> get(Climate.Sampler sampler, int x, int y, int z) {
        long key = key(x, y, z);
        Entry entry = entries[(int) HashCommon.mix(key) & MASK];
        return entry != null && entry.key == key && entry.sampler == sampler ? entry.value : null;
    }

    public void put(Climate.Sampler sampler, int x, int y, int z, Holder<Biome> value) {
        long key = key(x, y, z);
        entries[(int) HashCommon.mix(key) & MASK] = new Entry(sampler, key, value);
    }
}
