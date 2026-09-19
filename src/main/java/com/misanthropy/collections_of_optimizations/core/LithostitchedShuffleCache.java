package com.misanthropy.collections_of_optimizations.core;

import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public final class LithostitchedShuffleCache {

    public final StructurePoolElement[] elements;
    public final double[] exponent;
    public final double[] bias;

    public LithostitchedShuffleCache(StructurePoolElement[] elements, double[] exponent, double[] bias) {
        this.elements = elements;
        this.exponent = exponent;
        this.bias = bias;
    }
}
