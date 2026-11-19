package com.misanthropy.collections_of_optimizations.mixin.vanilla;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.BiomeLookupCache;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MixinMultiNoiseBiomeLookupCache {
    @Unique
    private volatile BiomeLookupCache coo$lookups;

    @Unique
    private BiomeLookupCache coo$cache() {
        BiomeLookupCache cache = this.coo$lookups;
        if (cache == null) {
            synchronized (this) {
                cache = this.coo$lookups;
                if (cache == null) {
                    cache = new BiomeLookupCache();
                    this.coo$lookups = cache;
                }
            }
        }
        return cache;
    }

    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("HEAD"), cancellable = true)
    private void coo$cachedLookup(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if (!CoOConfig.vanillaCacheBiomeLookups || sampler == null || !BiomeLookupCache.cacheable(x, y, z)) {
            return;
        }
        Holder<Biome> known = this.coo$cache().get(sampler, x, y, z);
        if (known != null) {
            cir.setReturnValue(known);
        }
    }

    @ModifyReturnValue(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;", at = @At("RETURN"))
    private Holder<Biome> coo$storeLookup(Holder<Biome> value, int x, int y, int z, Climate.Sampler sampler) {
        if (value != null && CoOConfig.vanillaCacheBiomeLookups && sampler != null && BiomeLookupCache.cacheable(x, y, z)) {
            this.coo$cache().put(sampler, x, y, z, value);
        }
        return value;
    }
}
