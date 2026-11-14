package com.misanthropy.collections_of_optimizations.mixin.elysiumapi;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.misanthropy.collections_of_optimizations.core.ElysiumReplacement;
import net.jadenxgamer.elysium_api.impl.biome.ElysiumBiomeSource;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1500)
public abstract class MixinMultiNoiseBiomeSourceReplacers {

    @ModifyReturnValue(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("RETURN")
    )
    private Holder<Biome> coo$applyBiomeReplacers(Holder<Biome> original, int x, int y, int z, Climate.Sampler sampler) {
        if (original == null || !(this instanceof ElysiumBiomeSource source) || source.getDimension() == null) {
            return original;
        }
        RegistryAccess access = ElysiumReplacement.access();
        if (access == null) {
            return original;
        }
        return ElysiumReplacement.replace(x, z, original, source.getDimension(), source.getWorldSeed(), access);
    }
}
