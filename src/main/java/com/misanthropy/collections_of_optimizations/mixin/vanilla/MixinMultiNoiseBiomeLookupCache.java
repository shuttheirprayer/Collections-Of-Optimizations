package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BiomeLookupCache;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MixinMultiNoiseBiomeLookupCache {
    @Unique
    private volatile BiomeLookupCache coo$lookups;

    @WrapMethod(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;")
    private Holder<Biome> coo$cachedLookup(int x, int y, int z, Climate.Sampler sampler, Operation<Holder<Biome>> original) {
        if (!CoOConfig.vanillaCacheBiomeLookups || sampler == null || !BiomeLookupCache.cacheable(x, y, z)) {
            return original.call(x, y, z, sampler);
        }
        BiomeLookupCache cache = this.coo$lookups;
        if (cache == null) {
            cache = new BiomeLookupCache();
            this.coo$lookups = cache;
        }
        Holder<Biome> known = cache.get(sampler, x, y, z);
        if (known != null) {
            return known;
        }
        Holder<Biome> value = original.call(x, y, z, sampler);
        if (value != null) {
            cache.put(sampler, x, y, z, value);
        }
        return value;
    }
}
