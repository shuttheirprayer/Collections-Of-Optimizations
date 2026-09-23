package com.misanthropy.collections_of_optimizations.mixin.elysiumapi;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.jadenxgamer.elysium_api.Elysium;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeHelper;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.jadenxgamer.elysium_api.impl.biome_replacer.BiomeReplacerDataDriven;
import net.jadenxgamer.elysium_api.impl.registry.ElysiumRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Random;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1500)
public abstract class MixinMultiNoiseBiomeSourceReplacers {

    @Unique
    private static final byte COO_UNKNOWN = 0;
    @Unique
    private static final byte COO_NONE = 1;
    @Unique
    private static final byte COO_SOME = 2;

    @Unique
    private volatile byte coo$replacerState;
    @Unique
    private List<ElysiumBiomeHelper.BiomeReplacer> coo$replacers;
    @Unique
    private List<BiomeReplacerDataDriven> coo$dataDrivenReplacers;

    @ModifyReturnValue(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("RETURN")
    )
    private Holder<Biome> coo$applyBiomeReplacers(Holder<Biome> original, int x, int y, int z, Climate.Sampler sampler) {
        byte state = this.coo$replacerState;
        if (state == COO_NONE) {
            return original;
        }
        if (!(this instanceof ElysiumBiomeSource source) || source.getDimension() == null) {
            return original;
        }
        if (state == COO_UNKNOWN) {
            RegistryAccess access = Elysium.registryAccess;
            if (access == null) {
                return original;
            }
            List<ElysiumBiomeHelper.BiomeReplacer> replacers = ElysiumBiomeHelper.biomesForDimension(source.getDimension());
            List<BiomeReplacerDataDriven> dataDriven = access.registryOrThrow(ElysiumRegistries.BIOME_REPLACER).stream().toList();
            this.coo$replacers = replacers;
            this.coo$dataDrivenReplacers = dataDriven;
            boolean none = replacers.isEmpty() && dataDriven.isEmpty();
            this.coo$replacerState = none ? COO_NONE : COO_SOME;
            if (none) {
                return original;
            }
        }
        return this.coo$replace(x, z, original, source.getWorldSeed());
    }

    @Unique
    private Holder<Biome> coo$replace(int x, int z, Holder<Biome> current, long worldSeed) {
        if (current == null) {
            return null;
        }
        for (ElysiumBiomeHelper.BiomeReplacer replacer : this.coo$replacers) {
            if (replacer.replaceBiomes().contains(current)
                    && new Random(coo$seed(x / replacer.size(), z / replacer.size(), worldSeed) ^ replacer.uniqueId().hashCode()).nextDouble() < replacer.rarity()) {
                return Elysium.registryAccess.registryOrThrow(Registries.BIOME).getHolderOrThrow(replacer.withBiome());
            }
        }
        for (BiomeReplacerDataDriven replacer : this.coo$dataDrivenReplacers) {
            if (replacer.replaceBiomes().contains(current)
                    && new Random(coo$seed(x / replacer.size(), z / replacer.size(), worldSeed) ^ replacer.uniqueId().hashCode()).nextDouble() < replacer.rarity()) {
                return replacer.withBiome();
            }
        }
        return current;
    }

    @Unique
    private static long coo$seed(int scaledX, int scaledZ, long worldSeed) {
        return (31L * scaledX + 17L ^ 37L * scaledZ + 23L ^ worldSeed) * 25214903917L;
    }
}
