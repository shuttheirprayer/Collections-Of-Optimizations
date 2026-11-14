package com.misanthropy.collections_of_optimizations.core;

import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.List;

public final class ElysiumReplacement {
    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long MASK = (1L << 48) - 1;

    private record DataDriven(RegistryAccess access, List<BiomeReplacerDataDriven> replacers) {
    }

    private static volatile DataDriven dataDriven;

    private ElysiumReplacement() {
    }

    public static Holder<Biome> replace(int x, int z, Holder<Biome> current, ResourceKey<LevelStem> dimension, long worldSeed, RegistryAccess access) {
        for (ElysiumBiomeHelper.BiomeReplacer replacer : ElysiumBiomeHelper.biomesForDimension(dimension)) {
            if (replacer.replaceBiomes().contains(current)
                    && nextDouble(seed(x / replacer.size(), z / replacer.size(), worldSeed) ^ replacer.uniqueId().hashCode()) < replacer.rarity()) {
                return access.registryOrThrow(Registries.BIOME).getHolderOrThrow(replacer.withBiome());
            }
        }
        for (BiomeReplacerDataDriven replacer : dataDriven(access)) {
            if (replacer.replaceBiomes().contains(current)
                    && nextDouble(seed(x / replacer.size(), z / replacer.size(), worldSeed) ^ replacer.uniqueId().hashCode()) < replacer.rarity()) {
                return replacer.withBiome();
            }
        }
        return current;
    }

    public static RegistryAccess access() {
        return Elysium.registryAccess;
    }

    private static List<BiomeReplacerDataDriven> dataDriven(RegistryAccess access) {
        DataDriven known = dataDriven;
        if (known == null || known.access() != access) {
            known = new DataDriven(access, access.registryOrThrow(ElysiumRegistries.BIOME_REPLACER).stream().toList());
            dataDriven = known;
        }
        return known.replacers();
    }

    private static long seed(int scaledX, int scaledZ, long worldSeed) {
        return (31L * scaledX + 17L ^ 37L * scaledZ + 23L ^ worldSeed) * 25214903917L;
    }

    static double nextDouble(long seed) {
        long state = (seed ^ MULTIPLIER) & MASK;
        state = (state * MULTIPLIER + 0xBL) & MASK;
        long high = state >>> 22;
        state = (state * MULTIPLIER + 0xBL) & MASK;
        long low = state >>> 21;
        return ((high << 27) + low) * 0x1.0p-53;
    }
}
