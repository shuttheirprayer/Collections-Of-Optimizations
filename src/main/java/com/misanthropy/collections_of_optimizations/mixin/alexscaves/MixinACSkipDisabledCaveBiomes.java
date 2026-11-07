package com.misanthropy.collections_of_optimizations.mixin.alexscaves;

import com.bawnorton.mixinsquared.TargetHandler;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.CaveBiomeGate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiNoiseBiomeSource.class, priority = 1500)
public abstract class MixinACSkipDisabledCaveBiomes {

    @TargetHandler(
            mixin = "com.github.alexmodguy.alexscaves.mixin.MultiNoiseBiomeSourceMixin",
            name = "citadel_getNoiseBiomeCoords"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            require = 0,
            remap = false
    )
    private void coo$skipWhenAllCaveBiomesDisabled(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir, CallbackInfo ci) {
        if (CoOConfig.alexscavesSkipDisabledBiomeHook && CaveBiomeGate.allDisabled) {
            ci.cancel();
        }
    }
}
