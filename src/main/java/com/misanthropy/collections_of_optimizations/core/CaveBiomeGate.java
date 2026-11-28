package com.misanthropy.collections_of_optimizations.core;

import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationNoiseCondition;
import com.misanthropy.collections_of_optimizations.mixin.alexscaves.ACBiomeConditionAccess;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class CaveBiomeGate {

    public static volatile boolean allDisabled;
    public static volatile Set<ResourceKey<Level>> listedDimensions;

    private CaveBiomeGate() {
    }

    public static void refresh() {
        allDisabled = !BiomeGenerationConfig.BIOMES.isEmpty()
                && BiomeGenerationConfig.BIOMES.values().stream().allMatch(BiomeGenerationNoiseCondition::isDisabledCompletely);
        Set<ResourceKey<Level>> listed = new HashSet<>();
        boolean known = !BiomeGenerationConfig.BIOMES.isEmpty();
        for (BiomeGenerationNoiseCondition condition : BiomeGenerationConfig.BIOMES.values()) {
            if (condition.isDisabledCompletely()) {
                continue;
            }
            java.util.List<String> names = ((ACBiomeConditionAccess) condition).coo$getDimensions();
            if (names == null) {
                known = false;
                break;
            }
            for (String name : names) {
                ResourceLocation id = ResourceLocation.tryParse(name);
                if (id == null) {
                    known = false;
                } else {
                    listed.add(ResourceKey.create(Registries.DIMENSION, id));
                }
            }
        }
        listedDimensions = known ? listed : null;
    }

    public static boolean dimensionUnlisted(ResourceKey<Level> dimension) {
        Set<ResourceKey<Level>> listed = listedDimensions;
        return dimension != null && listed != null && !listed.contains(dimension);
    }
}
