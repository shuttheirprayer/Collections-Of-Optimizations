package com.misanthropy.collections_of_optimizations.core;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

public final class BiomeLookupCache {
    private static final int BITS = 15;
    private static final int MASK = (1 << BITS) - 1;
    private static final int XZ_LIMIT = 1 << 23;
    private static final int Y_LIMIT = 1 << 11;

    private final ThreadLocal<Table> tables = ThreadLocal.withInitial(Table::new);

    private static final class Table {
        final long[] keys = new long[1 << BITS];
        final Object[] samplers = new Object[1 << BITS];
        final Object[] values = new Object[1 << BITS];
    }

    public static boolean cacheable(int x, int y, int z) {
        return x > -XZ_LIMIT && x < XZ_LIMIT && z > -XZ_LIMIT && z < XZ_LIMIT && y > -Y_LIMIT && y < Y_LIMIT;
    }

    private static long key(int x, int y, int z) {
        return ((long) (x & 0xFFFFFF) << 36) | ((long) (y & 0xFFF) << 24) | (z & 0xFFFFFF);
    }

    @SuppressWarnings("unchecked")
    public Holder<Biome> get(Climate.Sampler sampler, int x, int y, int z) {
        long key = key(x, y, z);
        int slot = (int) HashCommon.mix(key) & MASK;
        Table t = tables.get();
        return t.keys[slot] == key && t.samplers[slot] == sampler ? (Holder<Biome>) t.values[slot] : null;
    }

    public void put(Climate.Sampler sampler, int x, int y, int z, Holder<Biome> value) {
        long key = key(x, y, z);
        int slot = (int) HashCommon.mix(key) & MASK;
        Table t = tables.get();
        t.keys[slot] = key;
        t.samplers[slot] = sampler;
        t.values[slot] = value;
    }
}
