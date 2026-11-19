package com.misanthropy.collections_of_optimizations.core;

import com.misanthropy.collections_of_optimizations.mixin.vanilla.Xoroshiro128PlusPlusAccessor;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.XoroshiroPositionalFactoryAccessor;
import com.misanthropy.collections_of_optimizations.mixin.vanilla.XoroshiroRandomSourceAccessor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public final class ReusedPositionalRandom {
    private static final ThreadLocal<XoroshiroRandomSource> SOURCE = ThreadLocal.withInitial(() -> new XoroshiroRandomSource(0L, 1L));
    private static final boolean SAME = check();

    private ReusedPositionalRandom() {
    }

    public static RandomSource at(PositionalRandomFactory factory, int x, int y, int z) {
        if (!SAME || !(factory instanceof XoroshiroRandomSource.XoroshiroPositionalRandomFactory)) {
            return factory.at(x, y, z);
        }
        return reuse(factory, x, y, z);
    }

    private static RandomSource reuse(PositionalRandomFactory factory, int x, int y, int z) {
        XoroshiroPositionalFactoryAccessor seeds = (XoroshiroPositionalFactoryAccessor) factory;
        long lo = Mth.getSeed(x, y, z) ^ seeds.coo$seedLo();
        long hi = seeds.coo$seedHi();
        if ((lo | hi) == 0L) {
            lo = -7046029254386353131L;
            hi = 7640891576956012809L;
        }
        XoroshiroRandomSource source = SOURCE.get();
        Xoroshiro128PlusPlusAccessor state = (Xoroshiro128PlusPlusAccessor) ((XoroshiroRandomSourceAccessor) source).coo$generator();
        state.coo$setSeedLo(lo);
        state.coo$setSeedHi(hi);
        return source;
    }

    private static boolean check() {
        boolean same = selfCheck();
        if (!same) {
            com.mojang.logging.LogUtils.getLogger().warn("[CoO] reused surface gradient random gave different numbers than vanilla, using vanilla");
        }
        return same;
    }

    private static boolean selfCheck() {
        PositionalRandomFactory factory = new XoroshiroRandomSource(1234567L, -987654321L).forkPositional();
        for (int i = 0; i < 2000; i++) {
            int x = i * 7919 - 50000, y = (i % 400) - 64, z = i * -104729 + 3;
            if (factory.at(x, y, z).nextFloat() != reuse(factory, x, y, z).nextFloat()) {
                return false;
            }
        }
        return true;
    }
}
