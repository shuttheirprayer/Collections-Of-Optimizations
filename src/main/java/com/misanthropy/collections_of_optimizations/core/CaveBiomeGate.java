package com.misanthropy.collections_of_optimizations.core;

import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;

public final class CaveBiomeGate {

    public static volatile boolean allDisabled;

    private CaveBiomeGate() {
    }

    public static void refresh() {
        allDisabled = !BiomeGenerationConfig.BIOMES.isEmpty()
                && BiomeGenerationConfig.BIOMES.values().stream().allMatch(BiomeGenerationNoiseCondition::isDisabledCompletely);
    }
}
